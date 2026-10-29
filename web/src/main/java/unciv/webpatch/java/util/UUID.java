package unciv.webpatch.java.util;

/**
 * Adds the UUID(long, long) constructor to TeaVM's UUID, which stores the text form in a String field named value.
 *
 * The text is built digit by digit into a char array: the first version used string concatenation, and once
 * copied into TeaVM's class it produced only the first few characters (game IDs like "88af").
 */
public final class UUID {
    private String value;

    public UUID(long mostSigBits, long leastSigBits) {
        char[] text = new char[36];
        unciv$hex(text, 0, mostSigBits >>> 32, 8);
        text[8] = '-';
        unciv$hex(text, 9, mostSigBits >>> 16, 4);
        text[13] = '-';
        unciv$hex(text, 14, mostSigBits, 4);
        text[18] = '-';
        unciv$hex(text, 19, leastSigBits >>> 48, 4);
        text[23] = '-';
        unciv$hex(text, 24, leastSigBits, 12);
        value = new String(text);
    }

    /** Writes the lowest [digits] hex digits of [bits] into [text] at [offset] */
    private static void unciv$hex(char[] text, int offset, long bits, int digits) {
        for (int i = digits - 1; i >= 0; i--) {
            text[offset + i] = "0123456789abcdef".charAt((int) (bits & 15));
            bits >>>= 4;
        }
    }
}
