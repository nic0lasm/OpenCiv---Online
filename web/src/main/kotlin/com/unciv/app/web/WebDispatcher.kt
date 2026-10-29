package com.unciv.app.web

import com.unciv.utils.Log
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Delay
import kotlinx.coroutines.DisposableHandle
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.MainCoroutineDispatcher
import kotlinx.coroutines.Runnable
import org.teavm.jso.browser.Window
import kotlin.coroutines.CoroutineContext

/**
 * The browser has a single thread, so every dispatcher of the web build ends up here:
 * kotlinx' Dispatchers.Default/IO/Main as well as Unciv's own thread pools.
 *
 * Tasks are queued and run one after the other on a TeaVM green thread, the worker. Every [sliceMillis]
 * it sleeps for a moment, which hands the page back to the browser so it can repaint and run the render loop.
 * Long tasks can do the same in the middle through [yieldIfBusy] (Concurrency.yieldIfBusy in core): that's what
 * keeps the page alive while the AI plays its turns. Only the worker can be suspended that way, code called
 * from the render loop or a browser callback runs to the end.
 */
@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
object WebDispatcher : MainCoroutineDispatcher(), Delay {
    private const val sliceMillis = 25L

    private val queue = ArrayDeque<Runnable>()
    private var worker: Thread? = null
    private var sliceStart = 0L

    override val immediate: MainCoroutineDispatcher get() = this

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        queue.addLast(block)
        startWorker()
    }

    /** Runs the next queued task, if any. Used by the web version of runBlocking. */
    fun runNext(): Boolean {
        val task = queue.removeFirstOrNull() ?: return false
        try {
            task.run()
        } catch (ex: Throwable) {
            Log.error("Uncaught exception in a web task", ex)
        }
        return true
    }

    /** Starts the worker if it isn't running. It stops when the queue is empty, the next dispatch starts a new one. */
    private fun startWorker() {
        if (worker != null) return
        val thread = Thread(::work, "WebDispatcher")
        worker = thread
        thread.start()
    }

    private fun work() {
        sliceStart = System.currentTimeMillis()
        while (runNext()) yieldIfBusy()
        // Nothing can run between the queue check above and this line, the page has only this thread
        worker = null
    }

    /**
     * Lets the page repaint if the worker has been busy for more than [sliceMillis].
     * Does nothing anywhere else than on the worker, which is the only place where TeaVM can suspend.
     */
    fun yieldIfBusy() {
        if (Thread.currentThread() !== worker) return
        if (System.currentTimeMillis() - sliceStart < sliceMillis) return
        Thread.sleep(0)
        sliceStart = System.currentTimeMillis()
    }

    override fun scheduleResumeAfterDelay(timeMillis: Long, continuation: CancellableContinuation<Unit>) {
        val id = Window.setTimeout({ with(continuation) { resumeUndispatched(Unit) } }, timeMillis.toDouble())
        continuation.invokeOnCancellation { Window.clearTimeout(id) }
    }

    override fun invokeOnTimeout(timeMillis: Long, block: Runnable, context: CoroutineContext): DisposableHandle {
        val id = Window.setTimeout({ block.run() }, timeMillis.toDouble())
        return DisposableHandle { Window.clearTimeout(id) }
    }

    override fun toString() = "WebDispatcher"
}
