import { CommonModule } from '@angular/common'
import { moduleMetadata, type Meta, type StoryObj } from '@storybook/angular'
import { RollingRibbon } from '../app/shared/components/rolling-ribbon/rolling-ribbon'

type RollingRibbonStoryArgs = {
  items: string[]
  separator: string
  backgroundColor: string
  textColor: string
  highlightWord: string
  animationDuration: number
  styleClass: string
}

// Storybook configuration for Rolling Ribbon component
const meta: Meta<RollingRibbonStoryArgs> = {
  title: 'Components/Rolling Ribbon',
  tags: ['autodocs'],
  decorators: [
    moduleMetadata({
      imports: [CommonModule, RollingRibbon],
    }),
  ],
  argTypes: {
    items: {
      control: 'object',
      description: 'Array of text strings to display in the ribbon',
    },
    separator: {
      control: 'text',
      description: 'Character(s) to separate items',
    },
    backgroundColor: {
      control: 'color',
      description: 'Background color of the ribbon',
    },
    textColor: {
      control: 'select',
      options: [
        'primary-50',
        'primary-100',
        'primary-200',
        'primary-300',
        'primary-400',
        'primary-500',
        'primary-600',
        'primary-700',
        'primary-800',
        'primary-900',
        'primary-950',
      ],
      description: 'Text color using theme tokens',
    },
    highlightWord: {
      control: 'text',
      description: 'Optional word to highlight with pause-and-blink animation',
    },
    animationDuration: {
      control: { type: 'range', min: 5, max: 60, step: 1 },
      description: 'Duration of one complete scroll cycle in seconds',
    },
    styleClass: {
      control: 'text',
      description: 'Additional CSS classes to apply',
    },
  },
  args: {
    items: ['Welcome to RallyOn', 'Organize tournaments', 'Manage events', 'Stay connected'],
    separator: '|',
    backgroundColor: 'white',
    textColor: 'primary-950',
    highlightWord: '',
    animationDuration: 20,
    styleClass: '',
  },
}

export default meta
type Story = StoryObj<RollingRibbonStoryArgs>

const renderRibbon = (args: RollingRibbonStoryArgs) => ({
  props: args,
  template: `
    <div style="padding: 2rem; background: #f3f4f6;">
      <ro-rolling-ribbon
        [items]="items"
        [separator]="separator"
        [backgroundColor]="backgroundColor"
        [textColor]="textColor"
        [highlightWord]="highlightWord"
        [animationDuration]="animationDuration"
        [styleClass]="styleClass"
      ></ro-rolling-ribbon>
    </div>
  `,
})

// Default story with basic configuration
export const Default: Story = {
  args: {
    highlightWord: 'RallyOn!',
  },

  render: renderRibbon,
}

// Standard continuous scroll without highlight
export const ContinuousScroll: Story = {
  render: renderRibbon,
  args: {
    items: ['Breaking News', 'Latest Updates', 'Important Announcements', 'Stay Tuned'],
    separator: '•',
    backgroundColor: '#1e293b',
    textColor: 'primary-50',
    animationDuration: 15,
  },
}

// With highlight word animation
export const WithHighlight: Story = {
  render: renderRibbon,
  args: {
    items: ['Welcome', 'Explore features', 'Join the community', 'Get started'],
    separator: '|',
    backgroundColor: '#f8fafc',
    textColor: 'primary-900',
    highlightWord: 'RallyOn!',
    animationDuration: 12,
  },
}

// Fast scrolling ticker
export const FastTicker: Story = {
  render: renderRibbon,
  args: {
    items: ['QUICK', 'FAST', 'DYNAMIC', 'RAPID'],
    separator: '▸',
    backgroundColor: '#fef3c7',
    textColor: 'primary-800',
    animationDuration: 8,
  },
}

// Slow scrolling banner
export const SlowBanner: Story = {
  render: renderRibbon,
  args: {
    items: ['Take your time', 'Read carefully', 'Important information'],
    separator: '—',
    backgroundColor: '#dbeafe',
    textColor: 'primary-700',
    animationDuration: 30,
  },
}

// System status messages
export const SystemStatus: Story = {
  render: renderRibbon,
  args: {
    items: [
      'All systems operational',
      'Server uptime: 99.9%',
      'Last update: 5 minutes ago',
      'Next maintenance: Sunday 3AM',
    ],
    separator: '|',
    backgroundColor: '#d1fae5',
    textColor: 'primary-900',
    highlightWord: 'ONLINE',
    animationDuration: 20,
    styleClass: 'font-mono text-sm',
  },
}

// Event announcements
export const EventAnnouncements: Story = {
  render: renderRibbon,
  args: {
    items: [
      'Tournament starts in 2 days',
      'Registration now open',
      'Early bird discount available',
      'Limited spots remaining',
    ],
    separator: '★',
    backgroundColor: '#fce7f3',
    textColor: 'primary-800',
    highlightWord: 'REGISTER NOW',
    animationDuration: 18,
    styleClass: 'font-bold',
  },
}

// Custom styled variant
export const CustomStyled: Story = {
  render: renderRibbon,
  args: {
    items: ['Custom', 'Styled', 'Ribbon', 'Component'],
    separator: '◆',
    backgroundColor: '#0f172a',
    textColor: 'primary-50',
    highlightWord: '✨ Special ✨',
    animationDuration: 15,
    styleClass: 'font-doto text-3xl',
  },
}

// Comparison view - Multiple ribbons
export const Comparison: Story = {
  args: {
    items: ['Sample', 'Text', 'Items'],
  },
  render: (args) => ({
    props: args,
    template: `
      <div style="display: flex; flex-direction: column; gap: 1rem; padding: 2rem; background: #f9fafb;">
        <div>
          <h3 style="margin: 0 0 0.5rem 0; font-size: 0.875rem; font-weight: 600;">No Highlight</h3>
          <ro-rolling-ribbon
            [items]="['Message 1', 'Message 2', 'Message 3', 'Message 4']"
            [separator]="'|'"
            [backgroundColor]="'white'"
            [textColor]="'primary-950'"
            [highlightWord]="''"
            [animationDuration]="15"
          ></ro-rolling-ribbon>
        </div>
        
        <div>
          <h3 style="margin: 0 0 0.5rem 0; font-size: 0.875rem; font-weight: 600;">With Highlight (RallyOn!)</h3>
          <ro-rolling-ribbon
            [items]="['Message 1', 'Message 2', 'Message 3', 'Message 4']"
            [separator]="'|'"
            [backgroundColor]="'white'"
            [textColor]="'primary-950'"
            [highlightWord]="'RallyOn!'"
            [animationDuration]="15"
          ></ro-rolling-ribbon>
        </div>
        
        <div>
          <h3 style="margin: 0 0 0.5rem 0; font-size: 0.875rem; font-weight: 600;">Dark Theme</h3>
          <ro-rolling-ribbon
            [items]="['Dark mode', 'Looks great', 'Try it out']"
            [separator]="'•'"
            [backgroundColor]="'#1e293b'"
            [textColor]="'primary-50'"
            [highlightWord]="'✨'"
            [animationDuration]="12"
          ></ro-rolling-ribbon>
        </div>
        
        <div>
          <h3 style="margin: 0 0 0.5rem 0; font-size: 0.875rem; font-weight: 600;">Fast Ticker</h3>
          <ro-rolling-ribbon
            [items]="['QUICK', 'FAST', 'DYNAMIC']"
            [separator]="'▸'"
            [backgroundColor]="'#fef3c7'"
            [textColor]="'primary-800'"
            [highlightWord]="'GO!'"
            [animationDuration]="6"
          ></ro-rolling-ribbon>
        </div>
      </div>
    `,
  }),
}

// Interactive playground
export const Playground: Story = {
  render: renderRibbon,
  args: {
    items: ['Edit me', 'Change settings', 'Experiment', 'Have fun'],
    separator: '|',
    backgroundColor: '#ffffff',
    textColor: 'primary-950',
    highlightWord: 'Test!',
    animationDuration: 15,
    styleClass: '',
  },
}
