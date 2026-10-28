package com.unciv.app.web;

import java.util.Set;
import org.teavm.dependency.AbstractDependencyListener;
import org.teavm.dependency.DependencyAgent;
import org.teavm.dependency.MethodDependency;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderTransformer;
import org.teavm.model.ClassHolderTransformerContext;
import org.teavm.model.ClassReader;
import org.teavm.model.MethodHolder;
import org.teavm.model.MethodReader;
import org.teavm.model.MethodReference;
import org.teavm.model.ReferenceCache;
import org.teavm.model.ValueType;
import org.teavm.model.util.ModelUtils;
import org.teavm.parsing.ClassRefsRenamer;
import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

/**
 * Adds the JDK methods Unciv needs but TeaVM's class library lacks (e.g. Locale.forLanguageTag).
 *
 * For each class {@code a.b.C} listed below, the methods of {@code unciv.webpatch.a.b.C} that {@code a.b.C}
 * doesn't have yet are copied into it. Unlike a substitution, the rest of the class stays TeaVM's.
 * For the classes in {@link #replacingClasses}, the patch methods also replace existing ones.
 */
public class UncivTeaVMPlugin implements TeaVMPlugin {
    private static final String PATCH_PREFIX = "unciv.webpatch.";

    private static final Set<String> patchedClasses = Set.of(
        "java.util.Locale",
        "java.util.UUID",
        "java.util.Base64$Encoder",
        "java.util.Base64$Decoder",
        "java.lang.reflect.Field",
        "java.lang.Package",
        "java.lang.Thread",
        "java.lang.ClassLoader",
        "java.security.SecureRandom",
        "java.io.File",
        "java.nio.charset.Charset",
        "com.badlogic.gdx.graphics.Pixmap",
        "kotlin.ExceptionsKt__ExceptionsKt"
    );

    private static final Set<String> replacingClasses = Set.of(
        // stackTraceToString: give the JavaScript stack, the Java one is empty
        "kotlin.ExceptionsKt__ExceptionsKt"
    );

    @Override
    public void install(TeaVMHost host) {
        host.add(new MethodPatcher());
        host.add(new KeepNewInstanceClasses());
    }

    /**
     * Works around a TeaVM 0.15 crash (NPE in ClassInfoGenerator.writeSimpleConstructors): some classes
     * flow into Class.newInstance() as Class values without being kept in the program, mostly Kotlin
     * DefaultImpls and nested classes. Linking them keeps them, so the JS renderer finds them.
     */
    private static class KeepNewInstanceClasses extends AbstractDependencyListener {
        private static final MethodReference newInstance = new MethodReference(Class.class, "newInstance", Object.class);

        @Override
        public void methodReached(DependencyAgent agent, MethodDependency method) {
            if (!method.getReference().equals(newInstance)) return;
            method.getVariable(0).getClassValueNode().addConsumer(type -> {
                if (type.getValueType() instanceof ValueType.Object) {
                    String name = ((ValueType.Object) type.getValueType()).getClassName();
                    if (agent.getClassSource().get(name) != null) agent.linkClass(name);
                }
            });
        }
    }

    private static class MethodPatcher implements ClassHolderTransformer {
        private final ReferenceCache referenceCache = new ReferenceCache();

        @Override
        public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
            if (!patchedClasses.contains(cls.getName())) return;
            ClassReader patch = context.getHierarchy().getClassSource().get(PATCH_PREFIX + cls.getName());
            if (patch == null) return;

            ClassRefsRenamer renamer = new ClassRefsRenamer(referenceCache,
                name -> name.startsWith(PATCH_PREFIX) ? name.substring(PATCH_PREFIX.length()) : name);
            for (MethodReader method : patch.getMethods()) {
                // The patch class' own default constructor and static init are not part of the patch
                if (method.getName().equals("<clinit>")) continue;
                if (method.getName().equals("<init>") && method.parameterCount() == 0) continue;
                MethodHolder copy = renamer.rename(ModelUtils.copyMethod(method));
                MethodHolder existing = cls.getMethod(copy.getDescriptor());
                if (existing != null) {
                    if (!replacingClasses.contains(cls.getName())) continue;
                    cls.removeMethod(existing);
                }
                cls.addMethod(copy);
            }
        }
    }
}
