package com.unciv.app.web;

import com.badlogic.gdx.graphics.Pixmap;
import com.unciv.ui.components.fonts.FontFamilyData;
import com.unciv.ui.components.fonts.FontImplementation;
import com.unciv.ui.components.fonts.FontMetricsCommon;
import com.unciv.ui.components.fonts.Fonts;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import org.teavm.jso.JSBody;

/**
 * Glyphs drawn by the browser itself on an offscreen 2D canvas, the same way DesktopFont uses AWT.
 * The browser handles font fallback on its own, so emoji and CJK come for free.
 */
public class WebFont implements FontImplementation {

    private String cssFont = "";
    private int size = 1;
    private float ascent = 1f;
    private float descent = 0f;

    @Override
    public void setFontFamily(FontFamilyData fontFamilyData, int size) {
        this.size = size;
        String family = fontFamilyData.getInvariantName();
        // Mod fonts (filePath != null) would need a FontFace loaded from the virtual file system, not done yet
        if (family.isEmpty() || fontFamilyData.getFilePath() != null) family = "sans-serif";
        else family = "\"" + family + "\", sans-serif";
        cssFont = size + "px " + family;
        float[] m = measureFont(cssFont, size);
        ascent = m[0];
        descent = m[1];
    }

    @Override
    public int getFontSize() {
        return size;
    }

    @Override
    public Pixmap getCharPixmap(char c) {
        return getCharPixmap(String.valueOf(c));
    }

    @Override
    public Pixmap getCharPixmap(String symbolString) {
        int height = (int) Math.ceil(ascent + descent);
        int width = (int) Math.ceil(measureWidth(cssFont, symbolString));
        if (width <= 0) {
            // Same as desktop, e.g. for the Tab character
            height = size;
            width = size;
        }
        int[] argb = drawGlyph(cssFont, symbolString, width, height, ascent);
        return Fonts.INSTANCE.pixmapFromArgb(width, height, argb);
    }

    @Override
    public Sequence<FontFamilyData> getSystemFonts() {
        // Browsers don't list installed fonts without a permission prompt, offer the CSS generic families
        return SequencesKt.sequenceOf(
            new FontFamilyData("sans-serif", "sans-serif", null),
            new FontFamilyData("serif", "serif", null),
            new FontFamilyData("monospace", "monospace", null)
        );
    }

    @Override
    public FontMetricsCommon getMetrics() {
        return new FontMetricsCommon(ascent, descent, ascent + descent, 0f);
    }

    @JSBody(params = { "font", "size" }, script = ""
        + "var ctx = document.createElement('canvas').getContext('2d');"
        + "ctx.font = font;"
        + "var m = ctx.measureText('Hg');"
        + "var a = m.fontBoundingBoxAscent, d = m.fontBoundingBoxDescent;"
        + "if (a === undefined) { a = size * 0.8; d = size * 0.2; }"
        + "return [a, d];")
    private static native float[] measureFont(String font, int size);

    @JSBody(params = { "font", "text" }, script = ""
        + "var ctx = document.createElement('canvas').getContext('2d');"
        + "ctx.font = font;"
        + "return ctx.measureText(text).width;")
    private static native double measureWidth(String font, String text);

    /** @return the glyph as ARGB ints, the layout [Fonts.pixmapFromArgb] expects */
    @JSBody(params = { "font", "text", "w", "h", "ascent" }, script = ""
        + "var c = document.createElement('canvas'); c.width = w; c.height = h;"
        + "var ctx = c.getContext('2d');"
        + "ctx.font = font; ctx.fillStyle = '#fff'; ctx.textBaseline = 'alphabetic';"
        + "ctx.fillText(text, 0, ascent);"
        + "var d = ctx.getImageData(0, 0, w, h).data;"
        + "var out = new Array(w * h);"
        + "for (var i = 0; i < out.length; i++) {"
        + "  var p = i * 4;"
        + "  out[i] = ((d[p + 3] << 24) | (d[p] << 16) | (d[p + 1] << 8) | d[p + 2]);"
        + "}"
        + "return out;")
    private static native int[] drawGlyph(String font, String text, int w, int h, float ascent);
}
