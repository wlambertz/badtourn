import { applicationConfig, type Meta, type StoryObj } from '@storybook/angular'
import { provideRouter } from '@angular/router'
import { fn } from 'storybook/test'

import { NAV_PROFILE, NAV_QUICK_ACTION, NAV_SECTIONS } from '../app/layout/navigation/navigation.config'
import { DummySideNavbarComponent } from '../app/layout/navigation/dummy-side-navbar.component'
import type { SideNavSection } from '../app/layout/navigation/side-navbar.model'

const meta: Meta<DummySideNavbarComponent> = {
  title: 'Navigation/Side Navbar',
  component: DummySideNavbarComponent,
  tags: ['autodocs'],
  decorators: [
    applicationConfig({
      providers: [provideRouter([])],
    }),
  ],
  args: {
    sections: NAV_SECTIONS,
    quickAction: NAV_QUICK_ACTION,
    profile: NAV_PROFILE,
    linkSelected: fn(),
    actionSelected: fn(),
    logout: fn(),
    collapsedChange: fn(),
  },
}

export default meta
type Story = StoryObj<DummySideNavbarComponent>

export const Default: Story = {}

export const Collapsed: Story = {
  args: {
    collapsed: true,
  },
}

export const CustomData: Story = {
  args: {
    sections: [
      {
        label: 'My Workspace',
        items: [
          {
            label: 'Overview',
            icon: 'pi pi-home',
            route: '/overview',
            description: 'Snapshot for directors',
          },
          {
            label: 'Volunteers',
            icon: 'pi pi-id-card',
            route: '/volunteers',
            description: 'Assignments & check in',
            badge: '6',
          },
        ],
      },
    ] as SideNavSection[],
    quickAction: {
      label: 'Add volunteer shift',
      description: 'Fill remaining coverage gaps',
      icon: 'pi pi-clock',
      route: '/volunteers/new',
    },
  },
}
