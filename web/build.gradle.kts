import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.teavm.gradle.api.OptimizationLevel

// Browser build. Kotlin bytecode from core is translated to JavaScript by TeaVM,
// gdx-teavm provides the libGDX backend (WebGL, input, audio, asset preloading).
//
//   ./gradlew web:dist    builds web/build/dist/js/webapp
//   ./gradlew web:run     builds, then serves it on http://localhost:8080
//
// See web/README.md for what works and what doesn't yet.

plugins {
    // 1.6.x is the line built against libGDX 1.14.2 and TeaVM 0.15
    id("com.github.xpenatan.gdx-teavm") version "1.6.2"
}

// gdx-teavm and the TeaVM class library need 17, the code TeaVM translates can still be Java 8
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

gdxTeaVM {
    assets(rootProject.file("android/assets"))

    // libGDX Json fills game objects (rulesets, settings, saves) through reflection,
    // so TeaVM must keep the metadata for those classes.
    // Asking for com.unciv.** at once ran TeaVM out of memory, so it's kept to what the main menu needs.
    reflection(
        "com.unciv.models.**",
        "com.unciv.ui.screens.pickerscreens.PromotionScreenColors",
    )
    reflectionDebug.set(false)

    webDefaults {
        mainClass.set("com.unciv.app.web.WebLauncher")
        htmlTitle.set("Unciv")
        optimization.set(OptimizationLevel.NONE)
        obfuscated.set(false)
        outOfProcess.set(true)
        processMemory.set(8192)
    }

    js {}
}

tasks.register("dist") {
    group = "web"
    description = "Build the JavaScript version of Unciv into web/build/dist/js/webapp"
    dependsOn("gdx_teavm_web_js_build")
}

tasks.register("run") {
    group = "web"
    description = "Build the JavaScript version and serve it on http://localhost:8080"
    dependsOn("gdx_teavm_web_js_run")
}
