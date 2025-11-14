const defaultTheme = require('tailwindcss/defaultTheme');

/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/**/*.{html,ts,scss}',
    './src/stories/**/*.{ts,html,mdx}',
    './.storybook/**/*.{ts,html}',
  ],
  theme: {
    extend: {
      colors: {
        primary: 'var(--rallyon-primary)',
        'primary-dark': 'var(--rallyon-primary-dark)',
        surface: 'var(--rallyon-surface)',
        text: 'var(--rallyon-text)',
      },
      fontFamily: {
        sans: ['IBM Plex Sans', ...defaultTheme.fontFamily.sans],
        display: ['Space Grotesk', ...defaultTheme.fontFamily.sans],
        doto: ['Doto', 'Space Grotesk', ...defaultTheme.fontFamily.sans],
      },
    },
  },
  plugins: [],
};
