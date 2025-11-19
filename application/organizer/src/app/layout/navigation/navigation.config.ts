import { SideNavAction, SideNavProfile, SideNavSection } from './side-navbar.model'

export const NAV_SECTIONS: SideNavSection[] = [
  {
    label: 'Overview',
    items: [
      {
        label: 'Dashboard',
        icon: 'pi pi-compass',
        route: '/dashboard',
        description: 'Pulse on goals and quick tasks',
      },
      {
        label: 'Events',
        icon: 'pi pi-calendar',
        route: '/events',
        description: 'Schedule, publish, and archive events',
      },
      {
        label: 'Registrations',
        icon: 'pi pi-users',
        route: '/registrations',
        description: 'Manage team invites and statuses',
      },
    ],
  },
  {
    label: 'Operations',
    items: [
      {
        label: 'Messaging',
        icon: 'pi pi-comment',
        route: '/messaging',
        description: 'Send updates to participants',
        badge: 'New',
      },
      {
        label: 'Analytics',
        icon: 'pi pi-chart-line',
        route: '/analytics',
        description: 'Track engagement and revenue',
      },
      {
        label: 'Settings',
        icon: 'pi pi-cog',
        route: '/settings',
        description: 'Configuration and permissions',
      },
    ],
  },
]

export const NAV_QUICK_ACTION: SideNavAction = {
  label: 'Create an event',
  description: 'Launch a new bracket in minutes',
  icon: 'pi pi-plus-circle',
  route: '/events/new',
}

export const NAV_PROFILE: SideNavProfile = {
  name: 'Avery Organizer',
  role: 'Tournament Lead',
  email: 'avery@rallyon.tld',
  status: 'Trial workspace',
}
