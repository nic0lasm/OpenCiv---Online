package com.unciv.app.web

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

/**
 * Ktor engine of the web build. CIO needs sockets, which a page doesn't have, so for now every
 * request fails: multiplayer, mod downloads and update checks are unavailable in the browser.
 * A fetch() based engine would go here.
 */
object WebHttpEngineFactory : HttpClientEngineFactory<HttpClientEngineConfig> {
    override fun create(block: HttpClientEngineConfig.() -> Unit): HttpClientEngine =
        WebHttpEngine(HttpClientEngineConfig().apply(block))
}

class WebHttpEngine(override val config: HttpClientEngineConfig) : HttpClientEngine {
    override val dispatcher: CoroutineDispatcher get() = WebDispatcher
    override val coroutineContext: CoroutineContext = SupervisorJob() + WebDispatcher

    @io.ktor.utils.io.InternalAPI
    override suspend fun execute(data: HttpRequestData): HttpResponseData =
        throw UnsupportedOperationException("No network access in the browser build yet (${data.url})")

    override fun close() {}
}
