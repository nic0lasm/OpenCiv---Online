package com.unciv.app.web;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.unciv.UncivGame;
import com.unciv.ui.components.fonts.Fonts;
import com.unciv.ui.screens.basescreen.BaseScreen;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/**
 * Debugging help for the browser build, from the developer console:
 * {@code uncivDumpStage()} prints the actors of the current screen (class, position, size, visibility, label text),
 * {@code uncivFontInfo()} the pages of the glyph atlas and the characters on the last one.
 * Handy since there's no debugger stepping through TeaVM's output in any comfortable way.
 */
final class WebDebug {
    private WebDebug() {
    }

    @JSFunctor
    interface StringSupplier extends JSObject {
        String get();
    }

    static void install() {
        export("uncivDumpStage", WebDebug::dumpStage);
        export("uncivFontInfo", WebDebug::fontInfo);
    }

    /** Pages of the glyph atlas, and which characters sit on the last one */
    private static String fontInfo() {
        BitmapFont.BitmapFontData data = Fonts.INSTANCE.getFont().getData();
        int pages = Fonts.INSTANCE.getFont().getRegions().size;
        StringBuilder out = new StringBuilder("pages=").append(pages).append(" last page:");
        for (BitmapFont.Glyph[] block : data.glyphs) {
            if (block == null) continue;
            for (BitmapFont.Glyph glyph : block) {
                if (glyph != null && glyph.page == pages - 1)
                    out.append(' ').append((char) glyph.id).append('@').append(glyph.srcX).append(',').append(glyph.srcY);
            }
        }
        return out.toString();
    }

    private static String dumpStage() {
        if (UncivGame.Companion.isCurrentInitialized() && UncivGame.Companion.getCurrent().getScreen() != null) {
            BaseScreen screen = UncivGame.Companion.getCurrent().getScreen();
            StringBuilder out = new StringBuilder(screen.getClass().getName()).append('\n');
            for (Actor actor : screen.getStage().getActors()) dump(actor, 1, out);
            return out.toString();
        }
        return "No screen yet";
    }

    private static void dump(Actor actor, int depth, StringBuilder out) {
        for (int i = 0; i < depth; i++) out.append("  ");
        out.append(actor.getClass().getName());
        if (actor.getName() != null) out.append(" '").append(actor.getName()).append('\'');
        out.append(" x=").append((int) actor.getX()).append(" y=").append((int) actor.getY())
            .append(" w=").append((int) actor.getWidth()).append(" h=").append((int) actor.getHeight());
        if (!actor.isVisible()) out.append(" hidden");
        if (actor instanceof Label) out.append(" \"").append(((Label) actor).getText()).append('"');
        if (actor instanceof Image && ((Image) actor).getDrawable() == null) out.append(" no-drawable");
        out.append('\n');
        if (actor instanceof Group) {
            for (Actor child : ((Group) actor).getChildren()) dump(child, depth + 1, out);
        }
    }

    @JSBody(params = { "name", "f" }, script = "window[name] = function() { var s = f(); console.log(s); return s; };")
    private static native void export(String name, StringSupplier f);
}
