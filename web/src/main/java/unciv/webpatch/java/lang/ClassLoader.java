package unciv.webpatch.java.lang;

/** Adds ClassLoader.loadClass to TeaVM's ClassLoader. Only reached from kotlin's coroutine debug metadata. */
public final class ClassLoader {
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return Class.forName(name);
    }
}
