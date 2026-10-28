package unciv.webemu.kotlinx.coroutines;

import com.unciv.app.web.WebDispatcher;
import kotlinx.coroutines.CoroutineDispatcher;
import kotlinx.coroutines.MainCoroutineDispatcher;

/** Web build version of kotlinx.coroutines.Dispatchers: everything runs on the page's only thread. */
public final class Dispatchers {
    public static final Dispatchers INSTANCE = new Dispatchers();

    public static CoroutineDispatcher getDefault() {
        return WebDispatcher.INSTANCE;
    }

    public static MainCoroutineDispatcher getMain() {
        return WebDispatcher.INSTANCE;
    }

    public static CoroutineDispatcher getUnconfined() {
        return kotlinx.coroutines.Unconfined.INSTANCE;
    }

    public static CoroutineDispatcher getIO() {
        return WebDispatcher.INSTANCE;
    }

    public void shutdown() {
    }
}
