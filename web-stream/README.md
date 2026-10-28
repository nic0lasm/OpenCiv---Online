# Unciv in the browser, streamed

This is the quick way to get Unciv playable from a browser: the regular desktop build runs in a
container, and noVNC streams its screen to the page. Nothing in the game itself changes.

It's meant as a stopgap until the real web build (the `web` module, based on gdx-teavm) is usable.
Keep in mind each container is one game session, so this doesn't scale to many players.

## Running it on a server

You need a Linux box with Docker (and the compose plugin), plus a domain name pointing at it.
Ports 80 and 443 must be open, Caddy uses them to get a Let's Encrypt certificate.

```bash
cd web-stream
cp .env.example .env    # then fill in the domain and a real password
docker compose up -d --build
```

The first build takes a while (Gradle builds the jar inside the image). After that, open
`https://<your domain>/` and enter the password from `.env`.

Saves, settings and mods live in the `unciv-data` volume, so they survive restarts and rebuilds.

## Updating

```bash
git pull
docker compose up -d --build
```

## Locally

The `docker-compose.yml` at the repo root is still the simplest way to try it on your own machine,
see [Building locally](../docs/Developers/Building-Locally.md#running-via-docker).
