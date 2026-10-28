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

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
}

gdxTeaVM {
    assets(rootProject.file("android/assets"))

    // libGDX Json fills game objects (rulesets, settings, saves) through reflection,
    // so TeaVM must keep the metadata for those classes.
    reflection(
        "com.unciv.models.**",
        "com.unciv.logic.**",
        "com.unciv.ui.components.fonts.FontFamilyData",
        "com.unciv.json.**",
    )

    webDefaults {
        mainClass.set("com.unciv.app.web.WebLauncher")
        htmlTitle.set("Unciv")
        optimization.set(OptimizationLevel.NONE)
        obfuscated.set(false)
        processMemory.set(4096)
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
