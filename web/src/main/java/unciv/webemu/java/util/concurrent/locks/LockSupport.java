package unciv.webemu.java.util.concurrent.locks;

/**
 * Parking a thread is impossible in the page. kotlinx' JVM event loops reference this,
 * but in the web build their work goes through WebDispatcher, so these are never really used.
 */
public final class LockSupport {
    private LockSupport() {}

    public static void unpark(Thread thread) {}

    public static void park() {}

    public static void park(Object blocker) {}

    public static void parkNanos(long nanos) {}

    public static void parkNanos(Object blocker, long nanos) {}

    public static void parkUntil(long deadline) {}
}
