package unciv.webemu.org.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.helpers.NOPLogger;
import org.slf4j.helpers.NOPLoggerFactory;

/**
 * ktor logs through slf4j, whose real LoggerFactory looks for providers with class loaders and threads.
 * In the web build it always hands out the no-op logger.
 */
public final class LoggerFactory {
    public static final String PROVIDER_PROPERTY_KEY = "slf4j.provider";
    private static final ILoggerFactory factory = new NOPLoggerFactory();

    private LoggerFactory() {}

    public static Logger getLogger(String name) { return NOPLogger.NOP_LOGGER; }

    public static Logger getLogger(Class<?> clazz) { return NOPLogger.NOP_LOGGER; }

    public static ILoggerFactory getILoggerFactory() { return factory; }
}
