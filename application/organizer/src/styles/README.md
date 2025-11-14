# Global Styles Layout

This directory follows a lean ITCSS-inspired layering so we can scale gradually without dragging along unused boilerplate. The entry point is `../styles.scss`, which `@use`s `_index.scss` to compose the layers in a top-down (inverted triangle) order.

```
settings/   → design tokens, CSS variables, PrimeNG theme overrides
tools/      → mixins, functions, placeholder selectors (no emitted CSS)
generic/    → resets, normalize rules, base box-model tweaks
elements/   → unclassed HTML element styles (body, headings, anchors)
objects/    → low-level layout primitives (wrappers, grids, stacks)
components/ → shared widgets or cross-feature components
utilities/  → single-purpose classes, e.g., `.u-text-center`
```

Create new partials inside the relevant layer and import them from `_index.scss`. Keep feature-specific styles co-located with their Angular components and only promote reusable pieces into these global layers.
