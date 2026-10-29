package com.unciv.app.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.teavm.classlib.ReflectionContext;
import org.teavm.classlib.ReflectionSupplier;
import org.teavm.model.ClassReader;
import org.teavm.model.ElementModifier;
import org.teavm.model.FieldReader;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReader;

/**
 * Reflection metadata for the game state classes, the ones libGDX Json reads and writes in a save.
 *
 * gdx-teavm's own reflection list (build.gradle.kts) exposes every method of a class, and TeaVM then keeps
 * all of them because Method.invoke is reachable. For all of com.unciv.logic that ran TeaVM out of memory.
 * Json only needs fields and constructors, so that's all this exposes (plus enum constants).
 */
public class SaveReflectionSupplier implements ReflectionSupplier {
    private static final String[] packages = { "com.unciv.logic." };

    private static boolean isGameState(String className) {
        for (String prefix : packages) {
            if (className.startsWith(prefix)) return true;
        }
        return false;
    }

    @Override
    public Collection<String> getAccessibleFields(ReflectionContext context, String className) {
        if (!isGameState(className)) return Collections.emptyList();
        ClassReader cls = context.getClassSource().get(className);
        if (cls == null) return Collections.emptyList();
        List<String> fields = new ArrayList<>();
        for (FieldReader field : cls.getFields()) {
            // Enum constants too: some enums look up their own @Deprecated annotation through getField
            if (!field.hasModifier(ElementModifier.STATIC) || field.hasModifier(ElementModifier.ENUM))
                fields.add(field.getName());
        }
        return fields;
    }

    @Override
    public Collection<MethodDescriptor> getAccessibleMethods(ReflectionContext context, String className) {
        if (!isGameState(className)) return Collections.emptyList();
        ClassReader cls = context.getClassSource().get(className);
        if (cls == null) return Collections.emptyList();
        List<MethodDescriptor> constructors = new ArrayList<>();
        for (MethodReader method : cls.getMethods()) {
            if (method.getName().equals("<init>")) constructors.add(method.getDescriptor());
        }
        return constructors;
    }
}
