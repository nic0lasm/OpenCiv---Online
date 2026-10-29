package unciv.webemu.com.unciv.utils;

import com.unciv.app.web.WebDispatcher;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlinx.coroutines.CoroutineDispatcher;

/** Web build version of com.unciv.utils.ThreadPools: no threads, all of Concurrency's pools share the page's thread. */
public final class ThreadPools {
    public static final ThreadPools INSTANCE = new ThreadPools();

    public Pair<CoroutineDispatcher, Function0<Unit>> create(String threadPrefix, boolean isDaemon) {
        return new Pair<>(WebDispatcher.INSTANCE, () -> Unit.INSTANCE);
    }

    /** Lets the page repaint in the middle of long work, see WebDispatcher */
    public void yieldIfBusy() {
        WebDispatcher.INSTANCE.yieldIfBusy();
    }
}
