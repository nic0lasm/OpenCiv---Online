package com.unciv.app.web;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.teavm.backends.web.WebFiles;
import com.unciv.UncivGame;

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
    }

    // The JVM version asks java.lang.management, which TeaVM doesn't have
    @Override
    public int getGcCount() {
        return 0;
    }
}
