package unciv.webpatch.java.security;

/** Adds SecureRandom.getInstanceStrong to TeaVM's SecureRandom. */
public final class SecureRandom {
    public static java.security.SecureRandom getInstanceStrong() {
        return new java.security.SecureRandom();
    }
}
