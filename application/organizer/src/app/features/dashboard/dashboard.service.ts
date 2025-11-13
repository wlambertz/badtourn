import { Injectable, computed, signal } from '@angular/core';

export interface QuickAction {
  label: string;
  description: string;
  route: string;
  icon: string;
}

export interface UpcomingEvent {
  name: string;
  date: string;
  location: string;
  status: 'Draft' | 'Open' | 'Closed';
  registeredTeams: number;
}

export interface DashboardSummary {
  welcomeMessage: string;
  quickActions: QuickAction[];
  upcomingEvent: UpcomingEvent;
}

@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private readonly summaryState = signal<DashboardSummary>({
    welcomeMessage: 'Walk through the organizer journey, gather UX feedback, and prep automation.',
    quickActions: [
      {
        label: 'Create Event',
        description: 'Spin up a fresh bracket to test onboarding copy and flows.',
        route: '/events',
        icon: 'pi pi-plus-circle',
      },
      {
        label: 'Manage Brackets',
        description: 'Preview bracket seeds and simulate manual adjustments.',
        route: '/events',
        icon: 'pi pi-sitemap',
      },
      {
        label: 'View Registrations',
        description: 'Confirm player rosters and mark manual payments received.',
        route: '/settings',
        icon: 'pi pi-users',
      },
    ],
    upcomingEvent: {
      name: 'RallyOn Invitational - Beta Showcase',
      date: 'December 18, 2025 - 6:00 PM PST',
      location: 'Seattle HQ - War Room B',
      status: 'Draft',
      registeredTeams: 12,
    },
  });

  readonly summary = computed(() => this.summaryState());
}
