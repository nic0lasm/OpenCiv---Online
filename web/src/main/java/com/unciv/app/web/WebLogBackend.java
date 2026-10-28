package com.unciv.app.web;

import com.unciv.utils.DefaultLogBackend;
import com.unciv.utils.Tag;
import org.teavm.jso.JSBody;

/** Sends the game log to the browser console. */
public class WebLogBackend extends DefaultLogBackend {

    @Override
    public void debug(Tag tag, String curThreadName, String msg) {
        consoleLog("[" + tag.getName() + "] " + msg);
    }

    @Override
    public void error(Tag tag, String curThreadName, String msg) {
        consoleError("[" + tag.getName() + "] " + msg);
    }

    @Override
    public boolean isRelease() {
        return false;
    }

    @Override
    public String getSystemInfo() {
        return "Browser: " + userAgent();
    }

    @JSBody(params = "msg", script = "console.log(msg);")
    private static native void consoleLog(String msg);

    @JSBody(params = "msg", script = "console.error(msg);")
    private static native void consoleError(String msg);

    @JSBody(script = "return navigator.userAgent;")
    private static native String userAgent();
}
