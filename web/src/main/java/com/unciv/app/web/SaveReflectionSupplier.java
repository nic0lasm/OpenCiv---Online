package com.unciv.app.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.teavm.classlib.ReflectionContext;
import org.teavm.classlib.ReflectionSupplier;
import org.teavm.model.ClassReader;
import org.teavm.model.ElementModifier;
import org.teavm.model.FieldReader;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodReader;

/**
 * Reflection metadata for the classes libGDX Json fills outside com.unciv.models: the game state, which is
 * what a save holds, and a few data classes that live with the UI code but are read from rulesets or settings.
 *
 * gdx-teavm's own reflection list (build.gradle.kts) exposes every method of a class, and TeaVM then keeps
 * all of them because Method.invoke is reachable. For all of com.unciv.logic that ran TeaVM out of memory.
 * Json only needs fields and constructors, so that's all this exposes (plus enum constants).
 */
public class SaveReflectionSupplier implements ReflectionSupplier {
    private static final String[] packages = { "com.unciv.logic." };

    private static final Set<String> classes = Set.of(
        // civilopediaText of every ruleset object, tutorial tasks included
        "com.unciv.ui.screens.civilopediascreen.FormattedLine",
        // ModListCache.json
        "com.unciv.ui.screens.modmanager.ModUIData"
    );

    private static boolean isGameState(String className) {
        if (classes.contains(className)) return true;
        for (String prefix : packages) {
            if (className.startsWith(prefix)) return true;
        }
        // Empire overview pages keep their settings (sorting and such) in GameSettings
        return className.startsWith("com.unciv.ui.screens.overviewscreen.") && className.endsWith("PersistableData");
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
