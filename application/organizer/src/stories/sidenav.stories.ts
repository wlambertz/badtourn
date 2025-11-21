import type { Meta, StoryObj } from '@storybook/angular'

import { Sidenav } from '../app/layout/sidenav/sidenav'

const meta: Meta<Sidenav> = {
  title: 'Navigation/Sidenav',
  component: Sidenav,
  tags: ['autodocs'],
}

export default meta
type Story = StoryObj<Sidenav>

export const Playground: Story = {}
