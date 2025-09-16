# BadTourn

BadTourn – Das smarte System für Badmintonturniere

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