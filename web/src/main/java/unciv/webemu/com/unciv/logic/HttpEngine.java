package unciv.webemu.com.unciv.logic;

import com.unciv.app.web.WebHttpEngineFactory;
import io.ktor.client.engine.HttpClientEngineConfig;
import io.ktor.client.engine.HttpClientEngineFactory;

/** Web build version of com.unciv.logic.HttpEngine: keeps ktor's CIO engine (and its sockets) out of the build. */
public final class HttpEngine {
    public static final HttpEngine INSTANCE = new HttpEngine();

    public HttpClientEngineFactory<HttpClientEngineConfig> getFactory() {
        return WebHttpEngineFactory.INSTANCE;
    }
}
