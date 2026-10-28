package unciv.webemu.java.net;

/** Needed by ktor's exception hierarchy. (The JDK one extends SocketException, which TeaVM doesn't have either.) */
public class ConnectException extends java.io.IOException {
    public ConnectException() {}
    public ConnectException(String message) { super(message); }
}
