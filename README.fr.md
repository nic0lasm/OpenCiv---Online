# OpenCiv Online

[English](README.md) | Français

![](/extraImages/GithubPreviewImage.jpg)

OpenCiv Online est un fork d'[Unciv](https://github.com/yairm210/Unciv) avec un seul objectif : y jouer
dans un navigateur web, sans rien installer, seul ou entre amis.

Unciv est le remake libre et moddable de Civilization V pour Android et ordinateur, développé avec
[LibGDX](https://github.com/libgdx/libgdx) par Yair Morgenstern et de nombreux contributeurs. Presque
tout ce que vous trouverez ici vient de leur travail : le jeu, les règles, les graphismes, les
traductions. Ce fork ajoute seulement ce qu'il faut pour faire tourner le jeu sur le web. Si vous
voulez simplement jouer à Unciv sur votre téléphone ou votre ordinateur, prenez la version officielle
sur le [dépôt d'Unciv](https://github.com/yairm210/Unciv).

## Où on en est

Le fork est tout jeune, et rien n'est encore hébergé publiquement.

- **Démo en streaming (fonctionne déjà).** La version bureau habituelle tourne dans un conteneur
  Docker, et son écran est envoyé au navigateur avec noVNC. C'est une solution d'attente : c'est
  jouable, mais chaque joueur a besoin de son propre conteneur sur le serveur, et il y a un peu de
  latence.
- **Vraie version web (en cours).** Un nouveau module `web` basé sur
  [gdx-teavm](https://github.com/xpenatan/gdx-teavm), qui compile le bytecode JVM (Kotlin compris) en
  JavaScript ou en WebAssembly. Le jeu tourne alors entièrement dans le navigateur, comme un site
  statique.

Le plan détaillé, avec les difficultés techniques et les options écartées, est
[ici](https://claude.ai/artifact/8RFZVnVUiDh4b2bz8HHvWm).

## Feuille de route

On avance par petites versions, chacune avec un critère clair pour dire qu'elle est terminée.

| Version | Ce qu'elle apporte | Terminée quand |
|---|---|---|
| 0 | Démo en streaming derrière HTTPS | Une partie est jouable depuis une URL |
| 1 | Preuve de concept TeaVM : module `web`, tout `core` compile | Le menu principal s'affiche dans Chrome et Firefox |
| 2 | Couches de plateforme : dispatcher mono-thread, réflexion JSON, police via canvas, sauvegardes en IndexedDB, réseau via `Gdx.net` | Une partie solo tient 50 tours, et les sauvegardes survivent à un rechargement de la page |
| 3 | Multijoueur : CORS et HTTPS sur le serveur Unciv, chat en WebSocket, proxy pour télécharger les mods | Une partie entre un joueur sur navigateur et un joueur sur ordinateur |
| 4 | Finitions et mise en ligne : cache des assets, écran de chargement, tactile, import/export de sauvegardes, déploiement sur Pages, essai de la cible Wasm | Premier chargement en moins de 15 s en 4G, déploiement automatique |

Une règle tout du long : chaque modification de `core` passe par une interface, et la version JVM
reste intacte. Comme ça, les versions bureau et Android continuent de compiler, les tests existants
restent verts, et les fusions depuis Unciv restent gérables.

## Lancer le jeu

Il faut un JDK 11 ou plus récent.

**Sur ordinateur**, comme pour Unciv :

```bash
./gradlew desktop:run
```

**Dans un navigateur, en streaming depuis Docker** (la démo de la version 0, sur votre machine) :

```bash
docker build -t openciv-online .
docker run -d --name openciv -p 6901:6901 openciv-online
```

Ouvrez ensuite http://localhost:6901/vnc.html?password=headless. Le premier build prend un moment,
Gradle compile le jeu dans l'image. Plus de détails (garder ses sauvegardes, dépannage, client VNC
natif) dans [Building locally](docs/Developers/Building-Locally.md#running-via-docker) (en anglais).

**La version web** ne tourne pas encore. Les instructions arriveront ici avec la version 1.

## Contribuer

Les issues et pull requests sur la version web sont les bienvenues ici. Pour tout ce qui touche au jeu
lui-même (règles, IA, interface, traductions, mods), mieux vaut passer par Unciv, là où se trouvent
les gens qui connaissent ce code : commencez par la [documentation d'Unciv](https://yairm210.github.io/Unciv/)
et son [Discord](https://discord.gg/bjrB4Xw). Les corrections utiles à tout le monde seront proposées
à Unciv.

Pour signaler un bug, choisir une tâche ou proposer une pull request, voir [CONTRIBUTING.fr.md](CONTRIBUTING.fr.md).

## Licence et crédits

Ce fork garde les licences d'Unciv telles quelles.

Le code source est sous Mozilla Public License 2.0 (voir [LICENSE](LICENSE)), sauf mention contraire.
Concrètement, si vous hébergez une version de ce jeu, son code source (avec vos modifications) doit
rester disponible sous la même licence, et les mentions d'origine doivent être conservées.
TextureArraySpriteBatch.java, repris de
[carlislefox/libgdx-texture-array-batch](https://github.com/carlislefox/libgdx-texture-array-batch),
est sous CC0 1.0 Universal (voir [LICENSE-TextureArraySpriteBatch](LICENSE-TextureArraySpriteBatch)).

Les fichiers média ont été créés par de nombreuses personnes et sont sous un mélange de licences
CC BY-SA 4.0, CC BY 3.0/4.0, CC0 et domaine public (voir [docs/Credits.md](docs/Credits.md)).

Civilization est une marque de ses propriétaires. Ce projet n'a aucun lien avec Firaxis ou 2K et
n'utilise aucun de leurs contenus.

Merci à Yair Morgenstern et à tous ceux qui ont construit Unciv. Sans eux, il n'y aurait rien à mettre
en ligne.
