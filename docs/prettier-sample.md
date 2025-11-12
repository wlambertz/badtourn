# Prettier Sample Document

This doc showcases the expected formatting after running `npm run format`. Feel free to edit it when testing updates to our Prettier rules.

## Lists And Quotes

1. Change something locally.
2. Run `npm run format`.
3. Review the diff and commit if the output matches expectations.

- Bullet lists stay indented with two spaces.
- Inline code such as `printWidth` remains on one line.
- Block quotes are normalized when they span multiple lines.

> RallyOn docs should feel like they were authored by a single writer even when many contributors are involved.

## Tables

| Command                | Description                                   |
| ---------------------- | --------------------------------------------- |
| `npm run format`       | Applies Prettier to Markdown, MDX, JSON, etc. |
| `npm run format:check` | Runs Prettier in check mode (used by CI).     |

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
  <p><strong>Reminder:</strong> Prettier formats fenced code blocks and inline HTML consistently with our docs site renderer.</p>
</div>
