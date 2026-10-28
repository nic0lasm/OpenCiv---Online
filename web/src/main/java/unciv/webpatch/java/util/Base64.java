package unciv.webpatch.java.util;

import com.unciv.app.web.Base64Streams;

/** Adds the stream wrappers of Base64's encoder and decoder, used for zipped saves. */
public final class Base64 {
    public static final class Encoder {
        public java.io.OutputStream wrap(java.io.OutputStream os) {
            return new Base64Streams.EncodingOutputStream((java.util.Base64.Encoder) (Object) this, os);
        }
    }

    public static final class Decoder {
        public java.io.InputStream wrap(java.io.InputStream is) {
            return Base64Streams.decode((java.util.Base64.Decoder) (Object) this, is);
        }
    }
}
