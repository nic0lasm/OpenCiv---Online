package unciv.webemu.java.security;

import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;

/** TeaVM has no MessageDigest. Unciv only needs SHA-1 (the save game checksum), so that's all this one does. */
public class MessageDigest {
    private final String algorithm;
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    protected MessageDigest(String algorithm) {
        this.algorithm = algorithm;
    }

    public static MessageDigest getInstance(String algorithm) throws NoSuchAlgorithmException {
        String name = algorithm.toUpperCase().replace("-", "");
        if (!name.equals("SHA1") && !name.equals("SHA")) throw new NoSuchAlgorithmException(algorithm + " is not available in the web build");
        return new MessageDigest("SHA-1");
    }

    public final String getAlgorithm() { return algorithm; }

    public void update(byte input) { buffer.write(input); }

    public void update(byte[] input) { buffer.write(input, 0, input.length); }

    public void update(byte[] input, int offset, int len) { buffer.write(input, offset, len); }

    public void reset() { buffer.reset(); }

    public int getDigestLength() { return 20; }

    public byte[] digest(byte[] input) {
        update(input);
        return digest();
    }

    public byte[] digest() {
        byte[] result = sha1(buffer.toByteArray());
        buffer.reset();
        return result;
    }

    private static byte[] sha1(byte[] message) {
        int h0 = 0x67452301, h1 = 0xEFCDAB89, h2 = 0x98BADCFE, h3 = 0x10325476, h4 = 0xC3D2E1F0;
        long bitLength = (long) message.length * 8;
        int paddedLength = ((message.length + 8) / 64 + 1) * 64;
        byte[] padded = new byte[paddedLength];
        System.arraycopy(message, 0, padded, 0, message.length);
        padded[message.length] = (byte) 0x80;
        for (int i = 0; i < 8; i++) padded[paddedLength - 1 - i] = (byte) (bitLength >>> (8 * i));

        int[] w = new int[80];
        for (int chunk = 0; chunk < paddedLength; chunk += 64) {
            for (int i = 0; i < 16; i++) {
                int j = chunk + i * 4;
                w[i] = (padded[j] & 0xff) << 24 | (padded[j + 1] & 0xff) << 16 | (padded[j + 2] & 0xff) << 8 | (padded[j + 3] & 0xff);
            }
            for (int i = 16; i < 80; i++) w[i] = Integer.rotateLeft(w[i - 3] ^ w[i - 8] ^ w[i - 14] ^ w[i - 16], 1);

            int a = h0, b = h1, c = h2, d = h3, e = h4;
            for (int i = 0; i < 80; i++) {
                int f, k;
                if (i < 20) { f = (b & c) | (~b & d); k = 0x5A827999; }
                else if (i < 40) { f = b ^ c ^ d; k = 0x6ED9EBA1; }
                else if (i < 60) { f = (b & c) | (b & d) | (c & d); k = 0x8F1BBCDC; }
                else { f = b ^ c ^ d; k = 0xCA62C1D6; }
                int temp = Integer.rotateLeft(a, 5) + f + e + k + w[i];
                e = d; d = c; c = Integer.rotateLeft(b, 30); b = a; a = temp;
            }
            h0 += a; h1 += b; h2 += c; h3 += d; h4 += e;
        }

        byte[] out = new byte[20];
        int[] h = { h0, h1, h2, h3, h4 };
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 4; j++) out[i * 4 + j] = (byte) (h[i] >>> (24 - 8 * j));
        return out;
    }
}
