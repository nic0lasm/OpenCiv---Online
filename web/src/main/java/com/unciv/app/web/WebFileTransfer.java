package com.unciv.app.web;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;
import org.teavm.jso.typedarrays.Int8Array;

/**
 * Files in and out of the page: a download for saving, a file picker for loading.
 * Both have to start shortly after a click (browsers only allow them with "user activation").
 */
public final class WebFileTransfer {
    private WebFileTransfer() {
    }

    /** Hands [content] to the browser as a downloaded file called [fileName]. */
    public static void download(String fileName, String mimeType, byte[] content) {
        downloadNative(fileName, mimeType, Int8Array.copyFromJavaArray(content));
    }

    @JSBody(params = { "fileName", "mimeType", "content" }, script = ""
        + "var downloadUrl = URL.createObjectURL(new Blob([content], { type: mimeType }));"
        + "var downloadLink = document.createElement('a');"
        + "downloadLink.href = downloadUrl;"
        + "downloadLink.download = fileName;"
        + "downloadLink.style.display = 'none';"
        + "document.body.appendChild(downloadLink);"
        + "downloadLink.click();"
        + "document.body.removeChild(downloadLink);"
        + "setTimeout(function() { URL.revokeObjectURL(downloadUrl); }, 60000);")
    private static native void downloadNative(String fileName, String mimeType, Int8Array content);

    @JSFunctor
    public interface PickCallback extends JSObject {
        /** [content] is null when nothing was picked ([error] null too) or the file couldn't be read. */
        void picked(String fileName, Int8Array content, String error);
    }

    /**
     * Opens the browser's file picker and reads the chosen file. [callback] gets called once, later, from a
     * browser event. Browsers that don't report a cancelled picker (before Chrome 113, Safari 16.4) never call it then.
     */
    @JSBody(params = "callback", script = ""
        + "var fileInput = document.createElement('input');"
        + "fileInput.type = 'file';"
        + "fileInput.style.display = 'none';"
        + "document.body.appendChild(fileInput);"
        + "var answered = false;"
        + "function answer(fileName, content, error) {"
        + "  if (answered) return;"
        + "  answered = true;"
        + "  if (fileInput.parentNode) fileInput.parentNode.removeChild(fileInput);"
        + "  callback(fileName, content, error);"
        + "}"
        + "fileInput.addEventListener('cancel', function() { answer(null, null, null); });"
        + "fileInput.addEventListener('change', function() {"
        + "  var pickedFile = fileInput.files && fileInput.files[0];"
        + "  if (!pickedFile) { answer(null, null, null); return; }"
        + "  var reader = new FileReader();"
        + "  reader.onload = function() { answer(pickedFile.name, new Int8Array(reader.result), null); };"
        + "  reader.onerror = function() { answer(pickedFile.name, null, String(reader.error)); };"
        + "  reader.readAsArrayBuffer(pickedFile);"
        + "});"
        + "fileInput.click();")
    public static native void pickFile(PickCallback callback);
}
