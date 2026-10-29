package com.unciv.app.web

import com.github.xpenatan.gdx.teavm.backends.web.dom.typedarray.TypedArrays
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.HttpClientEngineCapability
import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.callContext
import io.ktor.client.engine.mergeHeaders
import io.ktor.client.plugins.HttpTimeoutCapability
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HeadersBuilder
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpProtocolVersion
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.OutgoingContent
import io.ktor.util.date.GMTDate
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.toByteArray
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Ktor engine of the web build, on top of the browser's fetch(). CIO needs sockets, which a page doesn't have. */
object WebHttpEngineFactory : HttpClientEngineFactory<HttpClientEngineConfig> {
    override fun create(block: HttpClientEngineConfig.() -> Unit): HttpClientEngine =
        WebHttpEngine(HttpClientEngineConfig().apply(block))
}

class WebHttpEngine(override val config: HttpClientEngineConfig) : HttpClientEngine {
    override val dispatcher: CoroutineDispatcher get() = WebDispatcher
    override val coroutineContext: CoroutineContext = SupervisorJob() + WebDispatcher
    // Accepted so clients installing HttpTimeout still work, the browser applies its own timeouts
    override val supportedCapabilities: Set<HttpClientEngineCapability<*>> = setOf(HttpTimeoutCapability)

    @OptIn(InternalAPI::class)
    override suspend fun execute(data: HttpRequestData): HttpResponseData {
        val callContext = callContext()
        val requestTime = GMTDate()

        val headers = StringBuilder()
        mergeHeaders(data.headers, data.body) { name, value ->
            // The browser sets these itself and refuses to let a page choose them
            if (!name.equals(HttpHeaders.UserAgent, true) && !name.equals(HttpHeaders.ContentLength, true))
                headers.append(name).append(": ").append(value).append('\n')
        }
        val body = data.body.bytes()?.let { TypedArrays.getInt8Array(it) }

        return suspendCancellableCoroutine { continuation ->
            WebFetch.fetch(data.method.value, data.url.toString(), headers.toString(), body) { status, statusText, responseHeaders, responseBody, error ->
                if (!continuation.isActive) return@fetch
                if (error != null) {
                    continuation.resumeWithException(IOException("Request to ${data.url} failed: $error"))
                    return@fetch
                }
                val bytes = if (responseBody == null) ByteArray(0) else TypedArrays.toByteArray(responseBody)
                continuation.resume(HttpResponseData(
                    HttpStatusCode(status, statusText ?: ""),
                    requestTime,
                    parseHeaders(responseHeaders),
                    HttpProtocolVersion.HTTP_1_1,
                    ByteReadChannel(bytes),
                    callContext
                ))
            }
        }
    }

    private suspend fun OutgoingContent.bytes(): ByteArray? = when (this) {
        is OutgoingContent.NoContent -> null
        is OutgoingContent.ByteArrayContent -> bytes()
        is OutgoingContent.ReadChannelContent -> readFrom().toByteArray()
        else -> throw UnsupportedOperationException("Request body ${this::class.simpleName} is not supported in the browser build")
    }

    override fun close() {}
}

/** Headers as fetch() and XMLHttpRequest give them: "name: value" per line */
internal fun parseHeaders(text: String) = HeadersBuilder().apply {
    for (line in text.lineSequence()) {
        val colon = line.indexOf(':')
        if (colon > 0) append(line.substring(0, colon).trim(), line.substring(colon + 1).trim())
    }
}.build()
