package unciv.webpatch.com.badlogic.gdx.graphics.g2d;

import java.nio.ByteBuffer;

/**
 * gdx-teavm keeps a pixmap's pixels twice: in the wasm heap, where drawing and getPixel work, and in a
 * ByteBuffer copy handed out by getPixels(). Every native operation copies heap to buffer afterwards, but
 * nothing copies the other way. Unciv writes glyphs and ruleset icons straight into getPixels(), so their
 * heap stayed blank: drawing them into the font atlas page copied nothing, and the first glyph of each new
 * page (uploaded with the whole page) was invisible, e.g. "oad game".
 *
 * Here, once the buffer has been handed out, it is copied back to the heap before each native operation.
 * Native methods below are only declarations of the existing ones, the patcher doesn't copy them.
 */
public class Gdx2DPixmapNative {
    int basePtr;
    private ByteBuffer buffer;
    /** The buffer has been handed out, so it may hold writes the heap doesn't have */
    private boolean bufferShared;

    public ByteBuffer getBuffer() {
        bufferShared = true;
        return buffer;
    }

    public void syncHeap() {
        if (bufferShared && buffer != null) copyToHeap();
    }

    public void clear(int color) {
        syncHeap();
        clear(basePtr, color);
        copyHeapToBuffer();
    }

    public void setPixel(int x, int y, int color) {
        syncHeap();
        setPixel(basePtr, x, y, color);
        copyHeapToBuffer();
    }

    public int getPixel(int x, int y) {
        syncHeap();
        return getPixel(basePtr, x, y);
    }

    public void drawLine(int x, int y, int x2, int y2, int color) {
        syncHeap();
        drawLine(basePtr, x, y, x2, y2, color);
        copyHeapToBuffer();
    }

    public void drawRect(int x, int y, int width, int height, int color) {
        syncHeap();
        drawRect(basePtr, x, y, width, height, color);
        copyHeapToBuffer();
    }

    public void drawCircle(int x, int y, int radius, int color) {
        syncHeap();
        drawCircle(basePtr, x, y, radius, color);
        copyHeapToBuffer();
    }

    public void fillRect(int x, int y, int width, int height, int color) {
        syncHeap();
        fillRect(basePtr, x, y, width, height, color);
        copyHeapToBuffer();
    }

    public void fillCircle(int x, int y, int radius, int color) {
        syncHeap();
        fillCircle(basePtr, x, y, radius, color);
        copyHeapToBuffer();
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        syncHeap();
        fillTriangle(basePtr, x1, y1, x2, y2, x3, y3, color);
        copyHeapToBuffer();
    }

    public void drawPixmap(int basePtr, int srcX, int srcY, int dstX, int dstY, int width, int height) {
        syncHeap();
        drawPixmap(basePtr, this.basePtr, srcX, srcY, width, height, dstX, dstY, width, height);
        copyHeapToBuffer();
    }

    public void drawPixmap(int basePtr, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight) {
        syncHeap();
        drawPixmap(basePtr, this.basePtr, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
        copyHeapToBuffer();
    }

    public void setBlend(int blend) {
        syncHeap();
        setBlend(basePtr, blend);
        copyHeapToBuffer();
    }

    public void setScale(int scale) {
        syncHeap();
        setScale(basePtr, scale);
        copyHeapToBuffer();
    }

    public native void copyToHeap();

    private native void copyHeapToBuffer();

    public static native void clear(int pixmap, int color);

    public static native void setPixel(int pixmap, int x, int y, int color);

    public static native int getPixel(int pixmap, int x, int y);

    public static native void drawLine(int pixmap, int x, int y, int x2, int y2, int color);

    public static native void drawRect(int pixmap, int x, int y, int width, int height, int color);

    public static native void drawCircle(int pixmap, int x, int y, int radius, int color);

    public static native void fillRect(int pixmap, int x, int y, int width, int height, int color);

    public static native void fillCircle(int pixmap, int x, int y, int radius, int color);

    public static native void fillTriangle(int pixmap, int x1, int y1, int x2, int y2, int x3, int y3, int color);

    public static native void drawPixmap(int src, int dst, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight);

    public static native void setBlend(int src, int blend);

    public static native void setScale(int src, int scale);
}
