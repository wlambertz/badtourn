# BadTourn

BadTourn – Das smarte System für Badmintonturniere

## Projektstruktur

```text
badtourn/
  application/                 App-Bootstrap, Zusammensetzung der Module (Composition Root)
    organizer/                 Oberfläche/Workflows für Organisator:innen
    audience/                  Öffentliche Ansichten für Zuschauer:innen ("Turnier-TV")
  service/
    player_mgmt/               Spielerverwaltung (PlayerManagement)
    scoring/                   Ergebnisdienst (ScoringService)
    tournament_mgmt/           Turnierverwaltung (TournamentManagement)
  3rd_party/
    authentication/            Authentifizierung/Identität (Integration)
    event_bus/                 Event-Bus/Integration
    search_engine/             Suche/Indexierung
  wiki/                        GitHub-Wiki als Submodul
  build.gradle                 Gradle Buildskript (Root)
  settings.gradle              Gradle Settings
  gradlew, gradlew.bat         Gradle Wrapper (Unix/Windows)
  gradle/wrapper/              Wrapper-Konfiguration
```

- Die fachlichen Module und ihre Verantwortlichkeiten sind im Wiki beschrieben: `wiki/Architecture/Modules.md`.
- Hohe Ebene der Module (Auszug):
  - Authentifizierung & Autorisierung: Benutzer, Rollen, Rechte.
  - Turnierverwaltung: Planung/Konfiguration/Spielpläne.
  - Spielerverwaltung: Registrierung und Pflege von Spielern/Teams.
  - Ergebnisdienst: Erfassung/Berechnung von Ergebnissen und Ranglisten.
- Öffentliche Informationen: Lesemodelle/Ansichten für Spieler/Zuschauer.

## Organizer-Portal (Angular 20)

Der Organizer-Client befindet sich in `application/organizer/` und wird mit Angular 20 sowie PrimeNG aufgebaut. Für einen schnellen Einstieg gibt es Root-NPM-Skripte:

- Abhängigkeiten installieren/aktualisieren: `npm run organizer:install`
- Dev-Server (http://localhost:4200): `npm run organizer:dev`
- Linting: `npm run organizer:lint`
- Unit-Tests lokal (öffnet Chrome): `npm run organizer:test`
- Headless-Testlauf für CI/PRs: `npm run organizer:test:ci`
- Playwright-Smoke: `npm run organizer:test:e2e` (oder `ro app test-e2e organizer`)
- Alternativ über die RallyOn-CLI: `ro app install|start|lint|test|test-ci organizer`

Die Skripte rufen intern die jeweiligen Kommandos im Unterprojekt auf, sodass nichts am Arbeitsverzeichnis gewechselt werden muss.

## API-Dokumentation

- **Swagger UI**: [Swagger UI (localhost)](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI JSON**: [`http://localhost:8080/v3/api-docs`](http://localhost:8080/v3/api-docs)
- **OpenAPI YAML**: [`http://localhost:8080/v3/api-docs.yaml`](http://localhost:8080/v3/api-docs.yaml)

Hinweis: Wenn ein `server.servlet.context-path` konfiguriert ist, wird dieser den Pfaden vorangestellt.

## Wiki-Submodul

Das GitHub-Wiki ist als Submodul im Ordner `wiki/` eingebunden.

- Initiales Klonen mit Submodulen:

  ```bash
  git clone --recurse-submodules https://github.com/wlambertz/badtourn.git
  ```

- Falls bereits geklont, Submodul initialisieren und holen:

  ```bash
  git submodule update --init --recursive
  ```

- Submodule auf den neuesten Stand bringen:

  ```bash
  git submodule update --remote --merge
  ```

- Änderungen im Wiki-Submodul committen/pushen (innerhalb von `wiki/`):

  ```bash
  cd wiki
  git status
  git add <dateien>
  git commit -m "Update wiki"
  git push
  cd ..
  ```

Hinweis: Änderungen am Submodul-Zeiger müssen im Haupt-Repo separat committed werden:

```bash
git add wiki
git commit -m "Update wiki submodule pointer"
```

## Windows: Zeilenenden (CRLF/LF)

Wenn auf Windows gearbeitet wird, kann Git beim Stagen/Committen Zeilenenden konvertieren. Um Warnungen zu vermeiden und konsistent zu bleiben:

- Empfohlene Git-Einstellung (global):

  ```bash
  git config --global core.autocrlf true
  ```

- Falls es nachträglich zu Mischungen kam, Inhalte einmalig normalisieren:

  ```bash
  git add --renormalize .
  git commit -m "Normalize line endings"
  ```

Optional kann eine `.gitattributes` mit Standard-Textbehandlung helfen:

```gitattributes
* text=auto
```
