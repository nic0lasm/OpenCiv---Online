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
        + "var h = new Headers();"
        + "headers.split('\\n').forEach(function(line) {"
        + "  var i = line.indexOf(':');"
        + "  if (i > 0) try { h.append(line.substring(0, i).trim(), line.substring(i + 1).trim()); } catch (e) {}"
        + "});"
        + "var init = { method: method, headers: h, redirect: 'follow' };"
        + "if (body !== null && method !== 'GET' && method !== 'HEAD') init.body = body;"
        + "try {"
        + "  fetch(url, init).then(function(r) {"
        + "    var hs = '';"
        + "    r.headers.forEach(function(v, k) { hs += k + ': ' + v + '\\n'; });"
        + "    return r.arrayBuffer().then(function(b) { callback(r.status, r.statusText, hs, new Int8Array(b), null); });"
        + "  }).catch(function(e) { callback(0, null, '', null, String(e)); });"
        + "} catch (e) { callback(0, null, '', null, String(e)); }")
    public static native void fetch(String method, String url, String headers, Int8Array body, Callback callback);

    /**
     * Synchronous request through XMLHttpRequest, for code that has to have the answer before it returns
     * (the multiplayer file storage). It freezes the page while it waits, and browsers warn about it.
     * The body is read as text. Calls [callback] before returning.
     */
    @JSBody(params = { "method", "url", "headers", "body", "callback" }, script = ""
        + "var x = new XMLHttpRequest();"
        + "try {"
        + "  x.open(method, url, false);"
        + "  headers.split('\\n').forEach(function(line) {"
        + "    var i = line.indexOf(':');"
        + "    if (i > 0) try { x.setRequestHeader(line.substring(0, i).trim(), line.substring(i + 1).trim()); } catch (e) {}"
        + "  });"
        + "  x.send(body === null || method === 'GET' ? null : body);"
        + "} catch (e) { callback(0, null, '', null, String(e)); return; }"
        + "var bytes = new TextEncoder().encode(x.responseText || '');"
        + "callback(x.status, x.statusText, x.getAllResponseHeaders(), new Int8Array(bytes.buffer), x.status === 0 ? 'Network error' : null);")
    public static native void requestSync(String method, String url, String headers, String body, Callback callback);
}
