package com.unciv.app.web

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext

object WebCoroutines {
    /**
     * Stand-in for kotlinx' runBlocking, which parks the calling thread - impossible in a browser.
     *
     * Instead we keep running queued tasks until the block is done. That covers Unciv's use of it
     * (wait for some background computation), but a block that waits for a timer, a network answer
     * or the GL thread can't finish this way, so that case fails loudly instead of freezing the tab.
     */
    @OptIn(ExperimentalCoroutinesApi::class, kotlinx.coroutines.DelicateCoroutinesApi::class)
    @JvmStatic
    fun <T> runBlocking(context: CoroutineContext, block: suspend CoroutineScope.() -> T): T {
        val fullContext = if (context[ContinuationInterceptor] == null) context + WebDispatcher else context
        val deferred = GlobalScope.async(fullContext, block = block)
        while (!deferred.isCompleted && WebDispatcher.runNext()) Unit
        check(deferred.isCompleted) { "runBlocking: the block waits for something that can't happen while we block (timer, network or GL thread)" }
        return deferred.getCompleted()
    }
}
