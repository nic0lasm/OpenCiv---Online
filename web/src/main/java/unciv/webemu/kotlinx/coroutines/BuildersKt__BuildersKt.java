package unciv.webemu.kotlinx.coroutines;

import com.unciv.app.web.WebCoroutines;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.EventLoop;

/** Web build version of the JVM-only part of kotlinx' Builders.kt, i.e. runBlocking. */
public final class BuildersKt__BuildersKt {

    public static <T> T runBlocking(CoroutineContext context, Function2<? super CoroutineScope, ? super Continuation<? super T>, ?> block) {
        return WebCoroutines.runBlocking(context, block);
    }

    @SuppressWarnings("unchecked")
    public static Object runBlocking$default(CoroutineContext context, Function2 block, int mask, Object unused) {
        if ((mask & 1) != 0) context = EmptyCoroutineContext.INSTANCE;
        return runBlocking(context, block);
    }

    public static <T> T runBlockingImpl(CoroutineContext context, EventLoop eventLoop, Function2<? super CoroutineScope, ? super Continuation<? super T>, ?> block) {
        return WebCoroutines.runBlocking(context, block);
    }
}
