package unciv.webpatch.com.badlogic.gdx.graphics;

/**
 * Adds Pixmap(ByteBuffer) to gdx-teavm's Pixmap. Only used for mod preview images downloaded from GitHub,
 * which the web build can't fetch anyway.
 */
public final class Pixmap {
    public Pixmap(int width, int height, com.badlogic.gdx.graphics.Pixmap.Format format) {
    }

    public Pixmap(java.nio.ByteBuffer encodedData) {
        this(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        throw new UnsupportedOperationException("Decoding images from a ByteBuffer is not available in the web build");
    }
}
