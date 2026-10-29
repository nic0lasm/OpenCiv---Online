package com.unciv.app.web;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.web.WebFiles;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetInstance;
import com.github.xpenatan.gdx.teavm.backends.web.dom.typedarray.TypedArrays;
import java.util.ArrayList;
import java.util.List;

/**
 * Which assets the page downloads before the game starts, and fetching the others when the game asks for them.
 *
 * The translation files are about two thirds of all assets (one per language, a game needs one), so they are
 * left out of gdx-teavm's preload list and fetched when the game first reads one.
 */
public final class WebAssets {
    private static final String TRANSLATIONS = "jsons/translations/";
    /** The language picker shows these numbers before any language is chosen */
    private static final String KEPT = TRANSLATIONS + "completionPercentages.properties";

    private WebAssets() {
    }

    /** @param path with or without a leading slash, the manifest has one, the game's file handles don't */
    static boolean isOnDemand(String path) {
        if (path.startsWith("/")) path = path.substring(1);
        // Includes the translators' notes (.md, .txt) that sit in the same folder: the game never reads them
        return path.startsWith(TRANSLATIONS) && !path.equals(KEPT);
    }

    /** Filters gdx-teavm's preload manifest, lines like "i:b:/jsons/translations/French.properties:555022:1" */
    public static String[] preloaded(String[] manifest) {
        List<String> kept = new ArrayList<>(manifest.length);
        for (String line : manifest) {
            String[] tokens = line.split(":");
            if (tokens.length < 3 || !isOnDemand(tokens[2].trim())) kept.add(line);
        }
        return kept.toArray(new String[0]);
    }

    /**
     * Makes sure an on-demand internal file is in gdx-teavm's memory storage before the game reads it.
     * The download is synchronous: the callers (translation loading) read the file right away.
     * Missing files stay missing, like on desktop.
     */
    static void ensureLoaded(WebFiles files, String path, FileHandle handle) {
        if (!isOnDemand(path) || handle.exists()) return;
        String url = AssetInstance.getLoaderInstance().getAssetUrl() + (path.startsWith("/") ? path.substring(1) : path);
        byte[][] result = new byte[1][];
        WebFetch.requestSync("GET", url, "", null, (status, statusText, headers, body, error) -> {
            if (error == null && status >= 200 && status < 300 && body != null)
                result[0] = TypedArrays.toByteArray(body);
        });
        if (result[0] == null) return;
        String key = path.startsWith("/") ? path : "/" + path;
        if (!key.endsWith("/")) key += "/";
        files.internalStorage.putFileInternal(key, result[0]);
    }
}
