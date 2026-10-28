package unciv.webpatch.java.lang.reflect;

/** Adds Field.getFloat to TeaVM's Field. */
public final class Field {
    public float getFloat(Object obj) throws IllegalAccessException {
        return ((Number) ((java.lang.reflect.Field) (Object) this).get(obj)).floatValue();
    }
}
