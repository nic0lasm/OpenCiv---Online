package com.unciv.app.web;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSExceptions;
import org.teavm.jso.JSObject;

/**
 * TeaVM keeps a JavaScript Error next to every Java exception, and only that one has a call stack.
 * The names in it are TeaVM's mangled ones, but they're readable enough to locate a problem.
 */
public final class WebStackTraces {
    private WebStackTraces() {}

    public static String describe(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        Throwable current = throwable;
        while (current != null) {
            if (current != throwable) sb.append("Caused by: ");
            sb.append(current).append('\n');
            String jsStack = jsStack(JSExceptions.getJSException(current));
            if (!jsStack.isEmpty()) sb.append(jsStack).append('\n');
            current = current.getCause() == current ? null : current.getCause();
        }
        return sb.toString();
    }

    @JSBody(params = "error", script = "return error && error.stack ? String(error.stack) : '';")
    private static native String jsStack(JSObject error);
}
