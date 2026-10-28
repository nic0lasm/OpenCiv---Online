package unciv.webemu.java.util.concurrent.atomic;

import java.util.Arrays;

/** Minimal AtomicReferenceArray for TeaVM, which lacks it. Single-threaded, so plain array access is atomic enough. */
public class AtomicReferenceArray<E> implements java.io.Serializable {
    private final Object[] array;

    public AtomicReferenceArray(int length) {
        array = new Object[length];
    }

    public AtomicReferenceArray(E[] array) {
        this.array = Arrays.copyOf(array, array.length, Object[].class);
    }

    public final int length() { return array.length; }

    @SuppressWarnings("unchecked")
    public final E get(int i) { return (E) array[i]; }

    public final void set(int i, E newValue) { array[i] = newValue; }

    public final void lazySet(int i, E newValue) { array[i] = newValue; }

    public final E getAndSet(int i, E newValue) {
        E old = get(i);
        array[i] = newValue;
        return old;
    }

    public final boolean compareAndSet(int i, E expect, E update) {
        if (array[i] != expect) return false;
        array[i] = update;
        return true;
    }

    public final boolean weakCompareAndSet(int i, E expect, E update) { return compareAndSet(i, expect, update); }

    @Override
    public String toString() { return Arrays.toString(array); }
}
