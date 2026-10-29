package unciv.webpatch.com.github.xpenatan.gdx.teavm.backends.web.assetloader;

import com.github.xpenatan.gdx.teavm.backends.web.assetloader.AssetLoaderListener;
import com.github.xpenatan.gdx.teavm.backends.web.assetloader.TeaAssetManifest;
import com.unciv.app.web.WebAssets;

/** Preloads gdx-teavm's generated asset list minus the files fetched on demand, see WebAssets. */
public class AssetLoadImpl {
    public void preload(AssetLoaderListener<Void> preloadListener) {
        preloadManifest("generated", WebAssets.preloaded(TeaAssetManifest.getAssets()), preloadListener);
    }

    private native void preloadManifest(String name, String[] lines, AssetLoaderListener<Void> preloadListener);
}
