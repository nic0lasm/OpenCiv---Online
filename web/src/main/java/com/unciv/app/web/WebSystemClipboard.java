package com.unciv.app.web;

import com.badlogic.gdx.utils.Clipboard;
import org.teavm.interop.Async;
import org.teavm.interop.AsyncCallback;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * Gives gdx-teavm's clipboard access to the system clipboard. Its own only sends text out after a permission
 * query (so the first copy got lost) and only sees text pasted with Ctrl+V on the page, which made
 * "Load copied data" useless for a save copied elsewhere.
 *
 * Copying goes to the page's clipboard as before, and to navigator.clipboard every time. Reading asks
 * navigator.clipboard when called on WebDispatcher's worker (the only place that can wait for the answer;
 * Unciv reads saves from the clipboard in a Concurrency.run block), the browser may ask the user first.
 * Anywhere else, or if that fails, it returns what the page's clipboard has.
 */
public class WebSystemClipboard implements Clipboard {
    private final Clipboard pageClipboard;

    public WebSystemClipboard(Clipboard pageClipboard) {
        this.pageClipboard = pageClipboard;
    }

    @Override
    public boolean hasContents() {
        return pageClipboard.hasContents() || canReadSystemClipboard();
    }

    @Override
    public String getContents() {
        if (canReadSystemClipboard() && WebDispatcher.INSTANCE.isOnWorker()) {
            try {
                String text = readSystemClipboard();
                if (text != null && !text.isEmpty()) return text;
            } catch (RuntimeException ex) {
                // Refused or unavailable: fall back to the page's clipboard
            }
        }
        return pageClipboard.getContents();
    }

    @Override
    public void setContents(String content) {
        pageClipboard.setContents(content);
        writeSystemClipboard(content);
    }

    @JSBody(script = "return !!(navigator.clipboard && navigator.clipboard.readText);")
    private static native boolean canReadSystemClipboard();

    @JSBody(params = "content", script = ""
        + "if (navigator.clipboard && navigator.clipboard.writeText)"
        + "  navigator.clipboard.writeText(content).catch(function(writeError) {});")
    private static native void writeSystemClipboard(String content);

    @Async
    private static native String readSystemClipboard();

    private static void readSystemClipboard(AsyncCallback<String> callback) {
        readSystemClipboardNative(callback::complete, error -> callback.error(new IllegalStateException(error)));
    }

    @JSFunctor
    interface TextCallback extends JSObject {
        void call(String text);
    }

    @JSBody(params = { "onText", "onError" }, script = ""
        + "navigator.clipboard.readText().then(function(clipboardText) { onText(clipboardText); },"
        + "  function(readError) { onError(String(readError)); });")
    private static native void readSystemClipboardNative(TextCallback onText, TextCallback onError);
}
