package unciv.webemu.java.util.concurrent.locks;

/**
 * TeaVM has no java.util.concurrent.locks. kotlinx.coroutines uses this one for a cache,
 * and with a single thread there is nothing to lock against.
 */
public class ReentrantReadWriteLock implements java.io.Serializable {
    private final ReadLock readerLock = new ReadLock();
    private final WriteLock writerLock = new WriteLock();

    public ReentrantReadWriteLock() {}

    public ReentrantReadWriteLock(boolean fair) {}

    public ReadLock readLock() { return readerLock; }

    public WriteLock writeLock() { return writerLock; }

    public int getReadHoldCount() { return 0; }

    public int getWriteHoldCount() { return 0; }

    public static class ReadLock implements java.io.Serializable {
        public void lock() {}
        public void lockInterruptibly() {}
        public boolean tryLock() { return true; }
        public void unlock() {}
    }

    public static class WriteLock implements java.io.Serializable {
        public void lock() {}
        public void lockInterruptibly() {}
        public boolean tryLock() { return true; }
        public void unlock() {}
    }
}
