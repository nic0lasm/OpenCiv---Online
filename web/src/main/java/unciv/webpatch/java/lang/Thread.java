package unciv.webpatch.java.lang;

/** Adds the few Thread methods TeaVM's Thread lacks. The page has exactly one thread. */
public final class Thread {
    public static int enumerate(java.lang.Thread[] threads) {
        if (threads.length == 0) return 0;
        threads[0] = java.lang.Thread.currentThread();
        return 1;
    }

    public void setContextClassLoader(ClassLoader classLoader) {
    }
}
