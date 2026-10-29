package unciv.webemu.com.unciv.logic.multiplayer.storage

import com.github.xpenatan.gdx.teavm.backends.web.dom.typedarray.TypedArrays
import com.unciv.app.web.WebFetch
import com.unciv.utils.debug

private typealias SendRequestCallback = (success: Boolean, result: String, code: Int?) -> Unit

/**
 * Web build version of com.unciv.logic.multiplayer.storage.SimpleHttp, same signatures.
 *
 * The multiplayer file storage expects the answer before the call returns, so this uses a synchronous
 * XMLHttpRequest: the page waits for the server, as the JVM thread did. Timeouts are the browser's,
 * which doesn't allow setting one on a synchronous request, and so is the User-Agent.
 * getIpAddress is left out: nothing calls it, and a page can't find its LAN address anyway.
 */
object SimpleHttp {
    fun sendGetRequest(url: String, timeout: Int = 5000, header: Map<String, String>? = null, action: SendRequestCallback) {
        sendRequest("GET", url, "", timeout, header, action)
    }

    @Suppress("UNUSED_PARAMETER")
    fun sendRequest(method: String, url: String, content: String, timeout: Int = 5000, header: Map<String, String>? = null, action: SendRequestCallback) {
        val fullUrl = if (url.contains("://")) url else "http://$url"
        val headers = StringBuilder("Content-Type: text/plain\n")
        for ((key, value) in header.orEmpty()) headers.append(key).append(": ").append(value).append('\n')

        var success = false
        var result = ""
        var code: Int? = null
        WebFetch.requestSync(method, fullUrl, headers.toString(), content.ifEmpty { null }) { status, _, _, body, error ->
            val text = if (body == null) "" else String(TypedArrays.toByteArray(body), Charsets.UTF_8)
            code = if (status == 0) null else status
            success = error == null && status in 200..299
            result = if (error != null) error else text
        }
        if (!success) debug("HTTP %s %s failed: %s %s", method, fullUrl, code, result)
        action(success, result, code)
    }
}
