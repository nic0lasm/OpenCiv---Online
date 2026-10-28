package com.unciv.app.web;

import com.github.xpenatan.gdx.teavm.backends.web.WebApplication;
import com.github.xpenatan.gdx.teavm.backends.web.WebApplicationConfiguration;
import com.unciv.logic.files.UncivFiles;
import com.unciv.ui.components.fonts.Fonts;
import com.unciv.utils.Display;
import com.unciv.utils.Log;

/** Entry point of the browser build, the TeaVM main class. */
public class WebLauncher {

    public static void main(String[] args) {
        Log.INSTANCE.setBackend(new WebLogBackend());
        Display.INSTANCE.setPlatform(new WebDisplay());
        Fonts.INSTANCE.setFontImplementation(new WebFont());
        // No file picker yet: saving/loading to a custom location stays disabled (PlatformSaverLoader.None)
        UncivFiles.Companion.setPreferExternalStorage(false);

        WebApplicationConfiguration config = new WebApplicationConfiguration();
        // 0 = use the whole browser window
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
        // Scene2d clipping relies on the stencil buffer
        config.stencil = true;
        config.useGL30 = true;
        new WebApplication(new WebGame(), config);
    }
}
