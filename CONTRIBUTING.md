# Contributing to OpenCiv Online

English | [Français](CONTRIBUTING.fr.md)

Thanks for stopping by. This fork has one job: getting Unciv to run in a web browser. Help on that is
very welcome, whether you write code, test in your browser or just report what breaks.

## Where things go

- **Something breaks in the browser**: open a [bug report](https://github.com/nic0lasm/OpenCiv---Online/issues/new?template=bug-report.yml).
  The browser console output (F12, Console tab) is the most useful thing you can paste.
- **An idea for the online version**: open an [idea](https://github.com/nic0lasm/OpenCiv---Online/issues/new?template=idea.yml).
- **The game itself** (rules, AI, UI, translations, mods): that's upstream, in
  [Unciv](https://github.com/yairm210/Unciv/issues). If it also happens on desktop or Android, it
  belongs there.

Issues can be written in English or French.

## Picking something to work on

The work is split by version, see the [roadmap](README.md#roadmap). Each version has a tracking
issue labelled `roadmap`, and each piece of work is a `task` with a "Done when" line.

- [`good first issue`](https://github.com/nic0lasm/OpenCiv---Online/issues?q=is%3Aopen+label%3A%22good+first+issue%22) are small and don't
  need to know the whole code base.
- [`help wanted`](https://github.com/nic0lasm/OpenCiv---Online/issues?q=is%3Aopen+label%3A%22help+wanted%22) are open to anyone.

Leave a comment on the issue before starting, so two people don't do the same thing. If you're stuck,
say so on the issue, it's fine.

## Building

JDK 11 or newer for desktop (17 for the web module). The desktop build and the tests work as in upstream:

```bash
./gradlew desktop:run
./gradlew tests:test
```

The web build lives in the `web` module, its README explains how to build and run it, and what
works so far. The streaming demo is described in the main [README](README.md#running-it).

## The one rule for `core`

The game code is shared with desktop and Android, and we want to keep merging from upstream Unciv.
So when the browser needs something different in `core`, put it behind a small interface (or a TeaVM
substitution in the `web` module) and leave the JVM behaviour exactly as it was. Desktop must still
build and the existing tests must still pass.

## Pull requests

- One pull request per topic, small if you can. Link the issue: `Closes #12`.
- Say how you checked it: which browser, what you clicked, which tests.
- Code style follows upstream, see [Coding standards](docs/Developers/Coding-standards.md).
- Fixes that make sense for everyone will be offered back to Unciv, so keep them clean of web-only
  hacks where possible.

By contributing you agree that your work is released under the same license as the project
([MPL 2.0](LICENSE)), and to follow the [code of conduct](CODE_OF_CONDUCT.md).
