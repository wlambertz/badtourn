# Prettier Sample Document

This file demonstrates the formatting rules enforced by `ro format` / `npm run format`. Feel free to tweak it when evaluating new Prettier settings.

## Lists And Quotes

1. Make a change in any Markdown/MDX/JSON/YAML file.
2. Run `ro format` (or `npm run format`) to apply the repo defaults.
3. Double-check the diff before committing.

- Bulleted lists stay at two spaces of indentation.
- Inline code such as `printWidth` remains on a single line.
- Block quotes wrap cleanly without manual line breaks.

> RallyOn docs should read as if a single author wrote them, even though many contributors collaborate.

## Tables

| Command             | Description                                  |
| ------------------- | -------------------------------------------- |
| `ro format`         | Rewrites Markdown, MDX, JSON, and YAML docs. |
| `ro format --check` | Runs Prettier in check mode (CI-equivalent). |

## Code Blocks

```bash
npm install
npm run format
git status
```

```json
{
  "printWidth": 120,
  "tabWidth": 2
}
```

## Embedded HTML

<div class="note">
  <p><strong>Reminder:</strong> Prettier handles inline HTML and fenced code without breaking the docs renderer.</p>
</div>
