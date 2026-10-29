package unciv.webpatch.java.util.concurrent;

/**
 * TeaVM's ThreadLocalRandom throws from setSeed, which Random's constructor calls, so its static
 * instance is never created and current() returns null. Kotlin's Random.Default relies on it.
 * Its numbers come from Math.random() anyway, so ignoring the seed is what the JDK does too.
 */
public class ThreadLocalRandom {
    public void setSeed(long seed) {
    }
}
