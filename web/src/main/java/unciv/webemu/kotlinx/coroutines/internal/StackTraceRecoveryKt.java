package unciv.webemu.kotlinx.coroutines.internal;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.CoroutineStackFrame;

/**
 * Web build version of kotlinx' stack trace recovery: leaves exceptions as they are.
 * The JVM version builds artificial stack frames from Exception().stackTrace[0] when it initializes,
 * and TeaVM exceptions don't carry Java stack frames.
 */
public final class StackTraceRecoveryKt {
    private StackTraceRecoveryKt() {}

    public static <E extends Throwable> E recoverStackTrace(E exception) { return exception; }

    public static <E extends Throwable> E recoverStackTrace(E exception, Continuation<?> continuation) { return exception; }

    public static Object recoverAndThrow(Throwable exception, Continuation<?> continuation) throws Throwable { throw exception; }

    public static <E extends Throwable> E unwrap(E exception) { return exception; }

    public static <E extends Throwable> E unwrapImpl(E exception) { return exception; }

    public static boolean isArtificial(StackTraceElement element) { return false; }

    public static void initCause(Throwable exception, Throwable cause) { exception.initCause(cause); }

    public static Throwable access$recoverFromStackFrame(Throwable exception, CoroutineStackFrame frame) { return exception; }
}
