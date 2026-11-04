package com.unciv.app.web

import com.unciv.logic.files.PlatformSaverLoader
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * "Save to custom location" downloads the save as a file, "Load from custom location" opens the browser's
 * file picker. A browser page can't write back to a file it read, so every save is a new download.
 */
class WebSaverLoader : PlatformSaverLoader {

    override fun requestSaveLocation(
        suggestedLocation: String,
        mimeType: String,
        onLocationChosen: (stream: OutputStream, location: String) -> Unit,
        onError: (ex: Exception) -> Unit
    ) {
        // The suggestion is a local path like SaveFiles/name, or the name of a file loaded earlier
        val fileName = suggestedLocation.substringAfterLast('/').ifEmpty { "Unciv save" }
        // The caller writes the save, then closes the stream: that's when it can go out as a download
        val stream = object : ByteArrayOutputStream() {
            private var downloaded = false
            override fun close() {
                super.close()
                if (downloaded) return
                downloaded = true
                WebFileTransfer.download(fileName, mimeType, toByteArray())
            }
        }
        onLocationChosen(stream, fileName)
    }

    override fun requestLoadLocation(
        onLocationChosen: (stream: InputStream, location: String) -> Unit,
        onError: (ex: Exception) -> Unit
    ) {
        WebFileTransfer.pickFile { fileName, content, error ->
            when {
                content != null -> onLocationChosen(ByteArrayInputStream(content.copyToJavaArray()), fileName)
                error != null -> onError(IOException("Could not read $fileName: $error"))
                else -> onError(PlatformSaverLoader.Cancelled())
            }
        }
    }
}
