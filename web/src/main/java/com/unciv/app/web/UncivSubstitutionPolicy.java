package com.unciv.app.web;

import java.util.Set;
import org.teavm.extension.spi.substitution.SimpleSubstitutionPolicy;
import org.teavm.extension.spi.substitution.SubstitutionSink;

/**
 * Tells TeaVM to compile the web build with our own versions of a few classes:
 * class {@code a.b.C} is replaced by {@code unciv.webemu.a.b.C} (see web/src/main/java/unciv/webemu).
 *
 * That's how the JVM-only parts (threads, sockets) stay out of the browser build
 * without changing what desktop, Android and the tests run.
 */
public class UncivSubstitutionPolicy extends SimpleSubstitutionPolicy {
    private static final Set<String> substituted = Set.of(
        // Worker threads -> the page's single thread
        "kotlinx.coroutines.Dispatchers",
        "kotlinx.coroutines.BuildersKt__BuildersKt",
        "com.unciv.utils.ThreadPools",
        // ktor CIO engine (sockets, TLS) -> an engine without network access
        "com.unciv.logic.HttpEngine",
        // ktor logs through slf4j, whose provider lookup uses class loaders
        "org.slf4j.LoggerFactory",
        // JDK classes TeaVM doesn't have at all
        "java.util.concurrent.atomic.AtomicReferenceArray",
        "java.util.concurrent.locks.ReentrantReadWriteLock",
        "java.util.concurrent.locks.ReentrantReadWriteLock$ReadLock",
        "java.util.concurrent.locks.ReentrantReadWriteLock$WriteLock",
        "java.util.concurrent.locks.LockSupport",
        "java.util.concurrent.RejectedExecutionException",
        "java.net.ConnectException",
        "java.nio.file.attribute.DosFileAttributes",
        "java.security.MessageDigest",
        "java.text.Collator"
    );

    @Override
    public void contribute(SubstitutionSink sink) {
        sink.selectClasses(substituted::contains).packagePrefix("unciv.webemu");
    }
}
