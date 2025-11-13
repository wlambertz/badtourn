# Organizer shell (Angular 20)

This folder hosts the RallyOn organizer portal shell that will power the login, dashboard, and navigation flows described in the UX spike. It is a standalone Angular 20 workspace that already has PrimeNG, PrimeFlex, and PrimeIcons wired up with the RallyOn base palette so feature work can begin immediately.

## Prerequisites

- Node.js 20+ (aligns with Angular 20 toolchain)
- npm 10+ (ships with Node 20)

Install dependencies the first time you clone the repo:

```bash
cd application/organizer
npm install
```

## Available npm scripts

| Script             | Purpose                                                             |
| ------------------ | ------------------------------------------------------------------- |
| `npm start`        | Run `ng serve` on `http://localhost:4200/` with live reload         |
| `npm run build`    | Production build output in `dist/organizer`                         |
| `npm test`         | Execute the default Karma/Jasmine unit suite (opens Chrome locally) |
| `npm run test:ci`  | Headless Karma run with `ChromeHeadless` and `--watch=false`        |
| `npm run lint`     | Lint TypeScript + template files via `@angular-eslint`              |
| `npm run test:e2e` | Playwright smoke that drives login → dashboard (requires Chromium)  |

## PrimeNG/branding bootstrap

- Global theme imports live in `src/styles.scss` (PrimeFlex utilities + PrimeIcons; theming is handled via `providePrimeNG`).
- Base typography (Inter) is registered in `src/index.html`.
- Login, dashboard, and stub routes showcase the brand palette plus PrimeNG components (Card, Button, Drawer, Tag, etc.).

## Playwright smoke tests

- Install browsers once: `npx playwright install --with-deps chromium`.
- Linux/WSL may need `sudo apt-get install libnspr4 libnss3 libasound2t64` to satisfy browser deps.
- Run the login walkthrough: `npm run test:e2e` (set `PLAYWRIGHT_CHROME_BIN` to reuse a system Chromium build).

## Organizer walkthrough

1. Hit `http://localhost:4200/login` and sign in with **organizer / rallyon**.
2. The PrimeNG drawer sidebar (Home, Events, Settings) can be toggled from the dashboard header or by calling the layout service.
3. The dashboard renders mock quick actions and the next upcoming event via `DashboardService`.
4. Events/Settings routes intentionally contain placeholders so UX testers can trace the end-to-end flow today.

## Scaffolding tips

- Generate standalone building blocks with `ng generate component name --standalone`.
- Group future modules under `src/app/features` and `src/app/shared` to mirror RallyOn bounded contexts.
- Run `npm run lint`, `npm run test`, `npm run test:ci`, and `npm run test:e2e` before committing to keep Angular flows green.

For more details, see the [Angular CLI docs](https://angular.dev/tools/cli) or the [PrimeNG setup guide](https://primeng.org/setup).
