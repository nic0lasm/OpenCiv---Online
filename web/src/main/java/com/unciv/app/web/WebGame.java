package com.unciv.app.web;

import com.unciv.UncivGame;

/** The browser flavour of [UncivGame]: only overrides what has no browser equivalent. */
public class WebGame extends UncivGame {

    public WebGame() {
        super(false);
    }

    // The JVM version asks java.lang.management, which TeaVM doesn't have
    @Override
    public int getGcCount() {
        return 0;
    }
}
