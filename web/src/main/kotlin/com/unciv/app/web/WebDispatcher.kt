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
 * Tasks are queued and run from a `setTimeout(0)` callback, a few milliseconds at a time
 * so the page keeps rendering while e.g. the rulesets load.
 */
@OptIn(InternalCoroutinesApi::class, ExperimentalCoroutinesApi::class)
object WebDispatcher : MainCoroutineDispatcher(), Delay {
    private const val sliceMillis = 25L

    private val queue = ArrayDeque<Runnable>()
    private var drainScheduled = false

    override val immediate: MainCoroutineDispatcher get() = this

    override fun dispatch(context: CoroutineContext, block: Runnable) {
        queue.addLast(block)
        scheduleDrain()
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

    private fun scheduleDrain() {
        if (drainScheduled) return
        drainScheduled = true
        Window.setTimeout({
            drainScheduled = false
            val end = System.currentTimeMillis() + sliceMillis
            while (System.currentTimeMillis() < end && runNext()) Unit
            if (queue.isNotEmpty()) scheduleDrain()
        }, 0)
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
