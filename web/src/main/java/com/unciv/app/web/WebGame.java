package com.unciv.app.web;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.teavm.backends.web.WebFiles;
import com.unciv.UncivGame;
import com.unciv.models.metadata.LocaleCode;
import java.util.Locale;
import org.teavm.jso.JSBody;

/** The browser flavour of [UncivGame]: only overrides what has no browser equivalent. */
public class WebGame extends UncivGame {

    public WebGame() {
        super(false);
    }

    @Override
    public void create() {
        // Before UncivFiles is created from Gdx.files: local files need their modification dates
        if (Gdx.files instanceof WebFiles) Gdx.files = new WebLocalFiles((WebFiles) Gdx.files);
        super.create();
        WebDebug.install();
    }

    /**
     * The browser's preferred languages, so the language picker shown on first launch starts on the right one.
     * Unciv only knows one locale per language (French is fr-FR), so a plain "fr" or "fr-CA" also means French.
     * Without a match, English.
     */
    @Override
    public Locale getDefaultLocale() {
        for (String tag : browserLanguages().split(",")) {
            if (tag.isEmpty()) continue;
            Locale locale = Locale.forLanguageTag(tag);
            LocaleCode exact = LocaleCode.Companion.find(locale);
            if (exact != null) return exact.locale();
            for (LocaleCode code : LocaleCode.values()) {
                if (!code.getUnused() && code.locale().getLanguage().equals(locale.getLanguage())) return code.locale();
            }
        }
        return LocaleCode.English.locale();
    }

    @JSBody(script = "var preferred = navigator.languages && navigator.languages.length ? navigator.languages : [navigator.language || ''];"
        + "return preferred.join(',');")
    private static native String browserLanguages();

    // The JVM version asks java.lang.management, which TeaVM doesn't have
    @Override
    public int getGcCount() {
        return 0;
    }
}
