package unciv.webpatch.com.badlogic.gdx.graphics.g2d;

/** Drawing one pixmap into another first brings the source's heap up to date, see Gdx2DPixmapNative. */
public class Gdx2DPixmap {
    private Gdx2DPixmapNative nativePixmap;

    public void drawPixmap(Gdx2DPixmap src, int srcX, int srcY, int dstX, int dstY, int width, int height) {
        src.nativePixmap.syncHeap();
        nativePixmap.drawPixmap(src.nativePixmap.basePtr, srcX, srcY, dstX, dstY, width, height);
    }

    public void drawPixmap(Gdx2DPixmap src, int srcX, int srcY, int srcWidth, int srcHeight, int dstX, int dstY, int dstWidth, int dstHeight) {
        src.nativePixmap.syncHeap();
        nativePixmap.drawPixmap(src.nativePixmap.basePtr, srcX, srcY, srcWidth, srcHeight, dstX, dstY, dstWidth, dstHeight);
    }
}
