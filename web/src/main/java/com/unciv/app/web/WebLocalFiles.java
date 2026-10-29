package com.unciv.app.web;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.web.WebFileHandle;
import com.github.xpenatan.gdx.teavm.backends.web.WebFiles;
import java.io.OutputStream;
import org.teavm.jso.JSBody;

/**
 * Wraps gdx-teavm's [WebFiles] so local files (settings, saves) know when they were last written.
 *
 * gdx-teavm keeps local files in IndexedDB but its [WebFileHandle.lastModified] always returns 0.
 * Unciv sorts saves by that date, and purges old autosaves by it, so with 0 everywhere it would
 * delete the newest autosaves. The dates live in the page's localStorage, one key per path.
 */
public class WebLocalFiles implements Files {
    private static final String KEY_PREFIX = "unciv-mtime:";

    private final WebFiles files;

    public WebLocalFiles(WebFiles files) {
        this.files = files;
    }

    @Override
    public FileHandle getFileHandle(String path, FileType type) {
        if (type == FileType.Local) return local(path);
        return files.getFileHandle(path, type);
    }

    @Override
    public FileHandle classpath(String path) {
        return files.classpath(path);
    }

    @Override
    public FileHandle internal(String path) {
        return files.internal(path);
    }

    @Override
    public FileHandle external(String path) {
        return files.external(path);
    }

    @Override
    public FileHandle absolute(String path) {
        return files.absolute(path);
    }

    @Override
    public FileHandle local(String path) {
        return new LocalFileHandle(path);
    }

    @Override
    public String getExternalStoragePath() {
        return files.getExternalStoragePath();
    }

    @Override
    public boolean isExternalStorageAvailable() {
        return files.isExternalStorageAvailable();
    }

    @Override
    public String getLocalStoragePath() {
        return files.getLocalStoragePath();
    }

    @Override
    public boolean isLocalStorageAvailable() {
        return files.isLocalStorageAvailable();
    }

    private class LocalFileHandle extends WebFileHandle {
        LocalFileHandle(String path) {
            super(files, path, FileType.Local);
        }

        private String key() {
            return KEY_PREFIX + path();
        }

        @Override
        public OutputStream write(boolean append, int bufferSize) {
            OutputStream output = super.write(append, bufferSize);
            setItem(key(), Long.toString(System.currentTimeMillis()));
            return output;
        }

        @Override
        public long lastModified() {
            if (!exists()) return 0;
            String value = getItem(key());
            if (value == null) return 0;
            try {
                return Long.parseLong(value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        @Override
        public boolean delete() {
            removeItem(key());
            return super.delete();
        }

        @Override
        public void moveTo(FileHandle dest) {
            String value = getItem(key());
            super.moveTo(dest);
            if (value != null && dest.type() == FileType.Local) setItem(KEY_PREFIX + dest.path(), value);
            removeItem(key());
        }

        @Override
        public FileHandle child(String name) {
            return local(super.child(name).path());
        }

        @Override
        public FileHandle parent() {
            return local(super.parent().path());
        }

        @Override
        public FileHandle sibling(String name) {
            return local(super.sibling(name).path());
        }
    }

    @JSBody(params = "key", script = "try { return window.localStorage.getItem(key); } catch (e) { return null; }")
    private static native String getItem(String key);

    @JSBody(params = { "key", "value" }, script = "try { window.localStorage.setItem(key, value); } catch (e) {}")
    private static native void setItem(String key, String value);

    @JSBody(params = "key", script = "try { window.localStorage.removeItem(key); } catch (e) {}")
    private static native void removeItem(String key);
}
