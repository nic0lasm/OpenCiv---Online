package com.unciv.logic

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.cio.CIO

/**
 * The ktor engine all of Unciv's [HttpClient][io.ktor.client.HttpClient]s are built on.
 *
 * CIO everywhere on the JVM. The web build has no sockets, so it replaces this object
 * at compile time with its own engine, see web/README.md.
 */
object HttpEngine {
    val factory: HttpClientEngineFactory<HttpClientEngineConfig> get() = CIO
}
