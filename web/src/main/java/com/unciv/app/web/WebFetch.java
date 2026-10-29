package com.unciv.app.web;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.Int8Array;

/**
 * The browser's two ways to make HTTP requests, for the web build's network code.
 *
 * Headers travel as one string, "name: value" per line, both ways. A failed request (network error,
 * CORS refusal, bad URL) reports status 0 and an error text; the browser doesn't say which it was.
 */
public final class WebFetch {
    private WebFetch() {
    }

    @JSFunctor
    public interface Callback extends JSObject {
        void done(int status, String statusText, String headers, Int8Array body, String error);
    }

    /** Asynchronous request through fetch(), for the ktor engine. */
    @JSBody(params = { "method", "url", "headers", "body", "callback" }, script = ""
        + "var requestHeaders = new Headers();"
        + "headers.split('\\n').forEach(function(headerLine) {"
        + "  var colon = headerLine.indexOf(':');"
        + "  if (colon > 0) try { requestHeaders.append(headerLine.substring(0, colon).trim(), headerLine.substring(colon + 1).trim()); } catch (headerError) {}"
        + "});"
        + "var requestInit = { method: method, headers: requestHeaders, redirect: 'follow' };"
        + "if (body !== null && method !== 'GET' && method !== 'HEAD') requestInit.body = body;"
        + "try {"
        + "  fetch(url, requestInit).then(function(response) {"
        + "    var responseHeaders = '';"
        + "    response.headers.forEach(function(headerValue, headerName) { responseHeaders += headerName + ': ' + headerValue + '\\n'; });"
        + "    return response.arrayBuffer().then(function(responseBody) { callback(response.status, response.statusText, responseHeaders, new Int8Array(responseBody), null); });"
        + "  }).catch(function(fetchError) { callback(0, null, '', null, String(fetchError)); });"
        + "} catch (fetchError) { callback(0, null, '', null, String(fetchError)); }")
    public static native void fetch(String method, String url, String headers, Int8Array body, Callback callback);

    /**
     * Synchronous request through XMLHttpRequest, for code that has to have the answer before it returns
     * (the multiplayer file storage). It freezes the page while it waits, and browsers warn about it.
     * The body is read as text. Calls [callback] before returning.
     */
    @JSBody(params = { "method", "url", "headers", "body", "callback" }, script = ""
        + "var request = new XMLHttpRequest();"
        + "try {"
        + "  request.open(method, url, false);"
        + "  headers.split('\\n').forEach(function(headerLine) {"
        + "    var colon = headerLine.indexOf(':');"
        + "    if (colon > 0) try { request.setRequestHeader(headerLine.substring(0, colon).trim(), headerLine.substring(colon + 1).trim()); } catch (headerError) {}"
        + "  });"
        + "  request.send(body === null || method === 'GET' ? null : body);"
        + "} catch (requestError) { callback(0, null, '', null, String(requestError)); return; }"
        + "var responseBytes = new TextEncoder().encode(request.responseText || '');"
        + "callback(request.status, request.statusText, request.getAllResponseHeaders(), new Int8Array(responseBytes.buffer), request.status === 0 ? 'Network error' : null);")
    public static native void requestSync(String method, String url, String headers, String body, Callback callback);
}
