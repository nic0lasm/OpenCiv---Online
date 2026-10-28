package com.unciv.app.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

/** Buffered versions of Base64.Encoder.wrap and Base64.Decoder.wrap, which TeaVM doesn't implement. */
public final class Base64Streams {
    private Base64Streams() {}

    /** Collects everything, encodes it on close. Fine for save files, which are written in one go. */
    public static final class EncodingOutputStream extends OutputStream {
        private final Base64.Encoder encoder;
        private final OutputStream target;
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        private boolean closed = false;

        public EncodingOutputStream(Base64.Encoder encoder, OutputStream target) {
            this.encoder = encoder;
            this.target = target;
        }

        @Override
        public void write(int b) {
            buffer.write(b);
        }

        @Override
        public void write(byte[] b, int off, int len) {
            buffer.write(b, off, len);
        }

        @Override
        public void close() throws IOException {
            if (closed) return;
            closed = true;
            target.write(encoder.encode(buffer.toByteArray()));
            target.close();
        }
    }

    public static InputStream decode(Base64.Decoder decoder, InputStream source) {
        try {
            ByteArrayOutputStream all = new ByteArrayOutputStream();
            byte[] chunk = new byte[8192];
            int n;
            while ((n = source.read(chunk)) > 0) all.write(chunk, 0, n);
            return new ByteArrayInputStream(decoder.decode(all.toByteArray()));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
