# Unciv in the browser (web module)

This module compiles Unciv to JavaScript with [TeaVM](https://teavm.org), using
[gdx-teavm](https://github.com/xpenatan/gdx-teavm) 1.6.2 as the libGDX backend (WebGL, input, audio, asset preloading).
It is not the noVNC streaming setup in `web-stream/`: here the game itself runs in the page.

Status: **phase 3.** In Chrome you can start a single-player game, play turns, save it, reload the page
and load it back. Settings and saves stay in the browser's storage (IndexedDB). The page can reach the network:
the mod list comes from GitHub, and an online multiplayer game can be created and played against an UncivServer
that allows the page's origin (see [Multiplayer](#multiplayer)).

![A saved game loaded back after reloading the page](screenshot-loaded-game.png)

## Building and running

```
./gradlew web:dist   # -> web/build/dist/js/webapp
./gradlew web:run    # same, then serves it on http://localhost:8080
```

Any static file server works for `web/build/dist/js/webapp`, e.g. `python3 -m http.server -d web/build/dist/js/webapp`.

The build needs JDK 17+ (gdx-teavm and TeaVM's class library require it) and takes 2-4 minutes;
TeaVM gets 8 GB (`processMemory`). `app.js` is minified, about 27 MB (under 5 MB gzipped). The assets the game
needs at startup are preloaded, about 10 MB; translations are fetched when a language is picked (see `WebAssets`).
TeaVM's optimizer is off: at BALANCED or AGGRESSIVE it crashes on the coroutine in `WorldScreen.nextTurn`.

## How it works

Core is unchanged in behaviour. The JVM-only parts are kept out of the browser build at compile time:

- **Substitutions** (`UncivSubstitutionPolicy`, TeaVM's `SubstitutionPolicy`): class `a.b.C` is compiled as
  `unciv.webemu.a.b.C` instead. Used for
  - `kotlinx.coroutines.Dispatchers` and `runBlocking`: everything runs on the page's single thread through
    `WebDispatcher` (a queue drained from `setTimeout`, a few ms at a time). `runBlocking` drains that queue until
    its block is done.
  - `com.unciv.utils.ThreadPools` and `com.unciv.logic.HttpEngine`, two small seams added to core for this:
    Concurrency's thread pools and the ktor engine (CIO on the JVM). In the browser, `WebHttpEngine` sends
    ktor's requests through `fetch()`.
  - `com.unciv.logic.multiplayer.storage.SimpleHttp`: the multiplayer file storage expects the server's answer
    before the call returns, so the web version uses a synchronous `XMLHttpRequest`. The page waits for the
    server, as the JVM thread did, and Chrome logs a deprecation warning about it.
  - `kotlinx.coroutines.internal.StackTraceRecoveryKt`, `org.slf4j.LoggerFactory`: both need real JVM stack
    frames or class loaders.
  - JDK classes TeaVM doesn't have: `AtomicReferenceArray`, `ReentrantReadWriteLock`, `LockSupport`,
    `RejectedExecutionException`, `ConnectException`, `DosFileAttributes`, `MessageDigest` (SHA-1 only),
    `Collator` (case-insensitive compare), `java.awt.Rectangle`.
- **Method patches** (`UncivTeaVMPlugin`): methods missing from TeaVM's version of a JDK class are copied in from
  `unciv.webpatch.*`: `Locale.forLanguageTag`, `UUID(long, long)`, `Base64` stream wrappers, `Field.getFloat`,
  `Package.getSpecificationVersion`, `Thread.enumerate` / `setContextClassLoader`, `ClassLoader.loadClass`,
  `SecureRandom.getInstanceStrong`, `File.toPath`, `Charset.isSupported`, `Pixmap(ByteBuffer)`.
  `Throwable.stackTraceToString()` is replaced so crash reports carry the JavaScript stack.
  `ThreadLocalRandom.setSeed` becomes a no-op: TeaVM's version throws from it during its own static init,
  so `current()` returned null and Kotlin's `Random.Default` crashed.
  libGDX's `Field` (gdx-teavm's version) gets real `isTransient`/`isStatic`/... answers and reads collection
  element types from TeaVM's generic signatures; without that, Json wrote `@Transient` fields into saves and
  read list or map elements back as raw maps.
  The plugin also works around a TeaVM 0.15 crash in its reflection metadata (NPE in
  `ClassInfoGenerator.writeSimpleConstructors`) by keeping classes that reach `Class.newInstance()`.
- **Reflection**: gdx-teavm's list (`reflection(...)` in `build.gradle.kts`) covers `com.unciv.models.**`.
  The game state, `com.unciv.logic`, goes through `SaveReflectionSupplier` instead, which only exposes fields
  and constructors: gdx-teavm's list exposes every method, and for `com.unciv.logic.**` that ran TeaVM out of memory.
  It also covers the few Json data classes living under `com.unciv.ui`, like `FormattedLine` (the civilopedia text
  of every ruleset object, tutorial tasks included).
- **Pixmaps**: gdx-teavm keeps a pixmap's pixels in the wasm heap and in a `ByteBuffer` copy, and only ever copies
  heap to buffer. Unciv writes glyphs and ruleset icons straight into `getPixels()`, so the patched
  `Gdx2DPixmapNative` copies the buffer back to the heap before native operations once it has been handed out.
  Without that, the first glyph of each new font atlas page was invisible.
- **Local files**: gdx-teavm keeps them in IndexedDB but reports 0 as modification date. `WebLocalFiles` wraps
  `Gdx.files` and keeps the dates in `localStorage`, so the save list is sorted and old autosaves are purged
  as on desktop.
- **Platform classes**: `WebLauncher`, `WebGame`, `WebFont` (glyphs drawn on an offscreen 2D canvas, the browser
  does font fallback), `WebLogBackend` (browser console), `WebDisplay`.

Small changes in core, all behaviour-preserving on the JVM:
`Log.getTag` copes with an empty stack trace, `TranslationCache` copes with `DecimalFormat` returning null affixes,
`KeyboardBinding.unCamelCase` doesn't use a regex replacement string (TeaVM rejects references to unmatched groups),
`UncivFiles` only asks for external files when external storage exists, and its debug logs fall back to the
plain path where `FileHandle.file()` isn't available.

## What works

- Loading: assets, atlases, skin, rulesets (Vanilla + G&K), translations
- Language picker and main menu, including the background map generation and its tile graphics
- Text rendering through the canvas font, mouse input
- Starting a new single-player game, playing turns (a few dozen ms per turn on a small map at the start)
- Autosaves, manual saves, the Load game screen and Resume, also after reloading the page
- Settings kept across reloads
- Network through `fetch()`: GitHub mod list, server checks, multiplayer game files
- Creating an online multiplayer game, playing turns and getting updates from an UncivServer with CORS enabled

## What doesn't (yet), roughly in the order to tackle it

- **Multiplayer servers** must allow the page's origin (CORS). The default server, uncivserver.xyz, doesn't yet,
  so from the browser it shows as unreachable.
- **Chat** goes through a WebSocket, which `WebHttpEngine` doesn't provide; the chat keeps trying to reconnect
  and logs errors. Same for anything else using ktor's WebSockets.
- **Mod downloads**: the list shows, downloading a mod isn't tried yet (zip files written to local storage).
- **Save files**: saves can't leave the browser yet. "Save to / Load from custom location" have no backend
  (`PlatformSaverLoader.None`), only the clipboard works. Clearing site data deletes the saves.
  No `FileType.External`, no `FileHandle.file()`: code outside the save path that calls it still throws.
- **Turns**: next-turn computation runs on the page's only thread, so the page freezes while the AI plays.
  Fine on a small map early on; a Web Worker is the real fix.
- **runBlocking** can't wait for timers, network or GL-thread work; it throws instead of freezing the tab.
- **Reflection** covers `com.unciv.models.**` and the game state. Code elsewhere that reflects on its own
  classes will fail with `NoSuchFieldException` and needs adding.
- **Fonts**: mod fonts (`FontFamilyData.filePath`) aren't loaded, the options only offer CSS generic families.
- **Collator**: no locale-aware sorting.
- **Audio**: untested, no music found at startup (as on a fresh desktop install).
- WebGL warns `glTexParameterf: Texture filter not recognized` (the atlases ask for a mipmap mag filter);
  harmless so far.
- Size: TeaVM's optimizer crashes on `WorldScreen.nextTurn` (see above), which would cut `app.js` further.
  Atlases, sounds and extra images are still all preloaded.

## Multiplayer

UncivServer refuses requests from web pages unless told which pages may call it:

```
java -jar UncivServer.jar -cors https://example.org
```

`-cors` (or the `UncivServerCorsOrigins` environment variable) takes a comma-separated list of origins, or `*`.
Without it nothing changes, and desktop or Android clients never need it since they don't send an `Origin` header.
Then set the server address in Options > Multiplayer in the browser.

## Debugging

Crashes show the JavaScript stack on the crash screen and in the console. TeaVM names are mangled but readable:
`culf_UncivFiles_getSave` is `com.unciv.logic.files.UncivFiles.getSave`.

The default build is minified: names are short and stacks unreadable. `./gradlew web:dist -PwebDebug=true` builds
with full names (larger, same speed to build).

When writing `@JSBody` scripts, give local variables long names. In the minified build TeaVM renames the parameters
to one or two letters without checking the script's own variables, so a local `c` silently replaces a parameter
that became `c`.

Two helpers are available from the developer console (`WebDebug`):
`uncivDumpStage()` lists the actors of the current screen with their position, size, visibility and label text,
and `uncivFontInfo()` shows how many font atlas pages exist and which characters are on the last one.
In a hidden tab or headless Chrome, `requestAnimationFrame` gets throttled to about one frame per second, and
gdx-teavm waits 60 frames before the first render, so the page stays black for a while there.
