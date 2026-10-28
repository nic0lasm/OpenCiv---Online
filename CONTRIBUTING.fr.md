# Contribuer à OpenCiv Online

[English](CONTRIBUTING.md) | Français

Merci de passer par ici. Ce fork a un seul but : faire tourner Unciv dans un navigateur. Toute aide
est la bienvenue, que vous écriviez du code, que vous testiez dans votre navigateur ou que vous
signaliez simplement ce qui casse.

## Où poster quoi

- **Quelque chose casse dans le navigateur** : ouvrez un [rapport de bug](https://github.com/nic0lasm/OpenCiv---Online/issues/new?template=bug-report.yml).
  La sortie de la console du navigateur (F12, onglet Console) est ce qu'il y a de plus utile à coller.
- **Une idée pour la version en ligne** : ouvrez une [idée](https://github.com/nic0lasm/OpenCiv---Online/issues/new?template=idea.yml).
- **Le jeu lui-même** (règles, IA, interface, traductions, mods) : ça se passe chez
  [Unciv](https://github.com/yairm210/Unciv/issues). Si ça arrive aussi sur ordinateur ou Android,
  c'est là-bas.

Les issues peuvent être écrites en français ou en anglais.

## Choisir quoi faire

Le travail est découpé par version, voir la [feuille de route](README.fr.md#feuille-de-route). Chaque
version a une issue de suivi avec le label `roadmap`, et chaque morceau de travail est une `task` avec
une ligne « Done when ».

- Les [`good first issue`](https://github.com/nic0lasm/OpenCiv---Online/issues?q=is%3Aopen+label%3A%22good+first+issue%22) sont petites et ne
  demandent pas de connaître tout le code.
- Les [`help wanted`](https://github.com/nic0lasm/OpenCiv---Online/issues?q=is%3Aopen+label%3A%22help+wanted%22) sont ouvertes à tous.

Laissez un commentaire sur l'issue avant de commencer, pour éviter que deux personnes fassent la même
chose. Si vous bloquez, dites-le sur l'issue, aucun souci.

## Compiler

JDK 11 ou plus récent pour la version bureau (17 pour le module web). La version bureau et les tests marchent comme chez Unciv :

```bash
./gradlew desktop:run
./gradlew tests:test
```

La version web est dans le module `web`, dont le README explique comment la compiler, la lancer et ce
qui marche déjà. La démo en streaming est décrite dans le [README](README.fr.md#lancer-le-jeu).

## La règle pour `core`

Le code du jeu est partagé avec les versions bureau et Android, et on veut pouvoir continuer à
récupérer les changements d'Unciv. Donc quand le navigateur a besoin de quelque chose de différent
dans `core`, on le met derrière une petite interface (ou une substitution TeaVM dans le module `web`)
et on laisse le comportement JVM exactement comme avant. La version bureau doit toujours compiler et
les tests existants doivent toujours passer.

## Pull requests

- Une pull request par sujet, petite si possible. Liez l'issue : `Closes #12`.
- Dites comment vous avez vérifié : quel navigateur, ce que vous avez testé, quels tests.
- Le style de code suit Unciv, voir [Coding standards](docs/Developers/Coding-standards.md).
- Les corrections utiles à tout le monde seront proposées à Unciv, donc évitez d'y mêler des bidouilles
  propres au web quand c'est possible.

En contribuant, vous acceptez que votre travail soit publié sous la même licence que le projet
([MPL 2.0](LICENSE)) et de respecter le [code de conduite](CODE_OF_CONDUCT.md).
