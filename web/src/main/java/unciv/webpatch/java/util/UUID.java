package unciv.webpatch.java.util;

/** Adds the UUID(long, long) constructor to TeaVM's UUID, which stores the text form in a String field named value. */
public final class UUID {
    private String value;

    public UUID(long mostSigBits, long leastSigBits) {
        value = unciv$hex(mostSigBits >> 32, 8) + "-" + unciv$hex(mostSigBits >> 16, 4) + "-" + unciv$hex(mostSigBits, 4)
            + "-" + unciv$hex(leastSigBits >> 48, 4) + "-" + unciv$hex(leastSigBits, 12);
    }

    private static String unciv$hex(long value, int digits) {
        long hi = 1L << (digits * 4);
        return Long.toHexString(hi | (value & (hi - 1))).substring(1);
    }
}
