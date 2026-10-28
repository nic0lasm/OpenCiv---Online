# OpenCiv Online

English | [Français](README.fr.md)

![](/extraImages/GithubPreviewImage.jpg)

OpenCiv Online is a fork of [Unciv](https://github.com/yairm210/Unciv) with one goal: play it in a
web browser, no install, solo or with friends.

Unciv is the open source, moddable remake of Civilization V for Android and desktop, built with
[LibGDX](https://github.com/libgdx/libgdx) by Yair Morgenstern and a lot of contributors. Pretty much
everything you'll find here is their work: the game, the rules, the art, the translations.
This fork only adds what it takes to run the game on the web. If you just want to play Unciv on your
phone or computer, get the official version from the [Unciv repository](https://github.com/yairm210/Unciv).

## Where things stand

The fork is young, and nothing is hosted publicly yet.

- **Streaming demo (works today).** The regular desktop build runs in a Docker container and its
  screen is streamed to the browser with noVNC. It's a stopgap: playable, but each player needs their
  own container on the server, and there's some input lag.
- **Real web build (in progress).** A new `web` module based on
  [gdx-teavm](https://github.com/xpenatan/gdx-teavm), which compiles the JVM bytecode (Kotlin included)
  to JavaScript or WebAssembly. The game then runs entirely in the browser, as a static site.

The detailed plan, with the technical hurdles and the options we ruled out, is
[here](https://claude.ai/artifact/8RFZVnVUiDh4b2bz8HHvWm).

## Roadmap

We move one small version at a time, each with a clear "done" criterion.

| Version | What it brings | Done when |
|---|---|---|
| 0 | Streaming demo behind HTTPS | A game is playable from a URL |
| 1 | TeaVM proof of concept: `web` module, all of `core` compiles | The main menu shows up in Chrome and Firefox |
| 2 | Platform layers: single-threaded dispatcher, JSON reflection, canvas font, IndexedDB saves, networking through `Gdx.net` | A new solo game lasts 50 turns, and saves survive a page reload |
| 3 | Multiplayer: CORS and HTTPS on the Unciv server, chat over WebSocket, mod download proxy | A game between a browser and a desktop player |
| 4 | Polish and release: asset caching, loading screen, touch input, save import/export, deploy to Pages, try the Wasm target | First load under 15 s on 4G, automatic deploys |

One rule throughout: every change to `core` goes behind an interface with the JVM version left
untouched, so desktop and Android keep building, the existing tests stay green, and merging from
upstream Unciv stays manageable.

## Running it

You'll need JDK 11 or newer.

**On your desktop**, same as upstream:

```bash
./gradlew desktop:run
```

**In a browser, streamed from Docker** (the version 0 demo, on your own machine):

```bash
docker build -t openciv-online .
docker run -d --name openciv -p 6901:6901 openciv-online
```

Then open http://localhost:6901/vnc.html?password=headless. The first build takes a while, Gradle
builds the game inside the image. More details (keeping saves, troubleshooting, native VNC client) in
[Building locally](docs/Developers/Building-Locally.md#running-via-docker).

**The web build** doesn't run yet. Instructions will land here with version 1.

## Contributing

Issues and pull requests about the web version are welcome on this repo. For anything about the game
itself (rules, AI, UI, translations, mods), it's better to go upstream, where the people who know that
code are: start with the [Unciv docs](https://yairm210.github.io/Unciv/) and its
[Discord](https://discord.gg/bjrB4Xw). Fixes that make sense for everyone will be offered back to
Unciv.

How to report a bug, pick a task and send a pull request: see [CONTRIBUTING.md](CONTRIBUTING.md).

## License and credits

This fork keeps Unciv's licensing as is.

The source code is under the Mozilla Public License 2.0 (see [LICENSE](LICENSE)), except where
otherwise noted. In practice, if you host a build of this game, its source code (with your changes)
has to stay available under the same license, and the original notices have to stay in place.
TextureArraySpriteBatch.java, forked from
[carlislefox/libgdx-texture-array-batch](https://github.com/carlislefox/libgdx-texture-array-batch),
is under CC0 1.0 Universal (see [LICENSE-TextureArraySpriteBatch](LICENSE-TextureArraySpriteBatch)).

The media files are made by many people and come under a mix of CC BY-SA 4.0, CC BY 3.0/4.0, CC0 and
public domain terms (see [docs/Credits.md](docs/Credits.md)).

Civilization is a trademark of its owners. This project isn't affiliated with Firaxis or 2K and uses
none of their assets.

Thanks to Yair Morgenstern and everyone who built Unciv. Without them there would be nothing to put
online.
