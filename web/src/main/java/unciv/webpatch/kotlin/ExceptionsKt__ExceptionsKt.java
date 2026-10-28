package unciv.webpatch.kotlin;

import com.unciv.app.web.WebStackTraces;

/** Replaces kotlin's Throwable.stackTraceToString(): TeaVM exceptions have no Java frames, so give the JavaScript ones. */
public final class ExceptionsKt__ExceptionsKt {
    public static String stackTraceToString(Throwable throwable) {
        return WebStackTraces.describe(throwable);
    }
}
