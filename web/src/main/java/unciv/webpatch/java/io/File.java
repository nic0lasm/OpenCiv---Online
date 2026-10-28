package unciv.webpatch.java.io;

/** Adds File.toPath to TeaVM's File. There is no NIO file system in the page, so it just says so. */
public final class File {
    public java.nio.file.Path toPath() {
        throw new UnsupportedOperationException("java.nio.file is not available in the web build");
    }
}
