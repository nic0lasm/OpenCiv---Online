package unciv.webpatch.com.badlogic.gdx.utils.reflect;

import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Replaces parts of gdx-teavm's libGDX Field, which libGDX Json uses to read and write saves.
 *
 * - gdx-teavm answers false to isTransient, isStatic and the like. Json would then write @Transient fields,
 *   which in the game state point back to their owners (endless recursion) and are rebuilt after loading anyway.
 * - Its getElementType only knows the classes in gdx-teavm's reflection list. Without it, the elements of a
 *   List or Map field in a save come back as raw Json maps. TeaVM keeps the generic signatures, so ask it first.
 */
public final class Field {
    private java.lang.reflect.Field field;

    public boolean isFinal() {
        return Modifier.isFinal(field.getModifiers());
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(field.getModifiers());
    }

    public boolean isProtected() {
        return Modifier.isProtected(field.getModifiers());
    }

    public boolean isPublic() {
        return Modifier.isPublic(field.getModifiers());
    }

    public boolean isStatic() {
        return Modifier.isStatic(field.getModifiers());
    }

    public boolean isTransient() {
        return Modifier.isTransient(field.getModifiers());
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(field.getModifiers());
    }

    public Class getElementType(int index) {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            Type[] arguments = ((ParameterizedType) genericType).getActualTypeArguments();
            if (index >= 0 && index < arguments.length) {
                Type argument = arguments[index];
                if (argument instanceof ParameterizedType) argument = ((ParameterizedType) argument).getRawType();
                if (argument instanceof Class) return (Class) argument;
            }
        }
        return com.badlogic.gdx.utils.reflect.FieldGen.getElementType(field.getDeclaringClass(), field.getName(), index);
    }
}
