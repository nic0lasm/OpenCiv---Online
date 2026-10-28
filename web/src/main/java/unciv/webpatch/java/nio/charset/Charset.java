package unciv.webpatch.java.nio.charset;

/** Adds Charset.isSupported to TeaVM's Charset. */
public final class Charset {
    public static boolean isSupported(String charsetName) {
        try {
            java.nio.charset.Charset.forName(charsetName);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }
}
