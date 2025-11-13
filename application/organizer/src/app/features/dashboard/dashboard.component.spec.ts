import { computed, signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { LayoutService } from '../../core/services/layout.service';
import { DashboardComponent } from './dashboard.component';
import { DashboardService, DashboardSummary } from './dashboard.service';

const summaryFixture: DashboardSummary = {
  welcomeMessage: 'Test message',
  quickActions: [
    { label: 'Action One', description: 'Desc', route: '/a', icon: 'pi pi-plus' },
    { label: 'Action Two', description: 'Desc 2', route: '/b', icon: 'pi pi-users' },
  ],
  upcomingEvent: {
    name: 'Mock Event',
    date: 'Tomorrow',
    location: 'HQ',
    status: 'Draft',
    registeredTeams: 4,
  },
};

class DashboardServiceStub {
  private readonly state = signal(summaryFixture);
  summary = computed(() => this.state());
}

class LayoutServiceStub {
  openSidebar = jasmine.createSpy('openSidebar');
}

describe('DashboardComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardComponent, RouterTestingModule],
      providers: [
        { provide: DashboardService, useClass: DashboardServiceStub },
        { provide: LayoutService, useClass: LayoutServiceStub },
      ],
    }).compileComponents();
  });

  it('should render quick actions defined by the dashboard service', () => {
    const fixture = TestBed.createComponent(DashboardComponent);
    fixture.detectChanges();
    const cards = fixture.nativeElement.querySelectorAll('.dashboard__action-card');
    expect(cards.length).toBe(summaryFixture.quickActions.length);
  });

  it('should invoke the layout service when the sidebar button is clicked', () => {
    const fixture = TestBed.createComponent(DashboardComponent);
    const layoutService = TestBed.inject(LayoutService) as unknown as LayoutServiceStub;
    fixture.detectChanges();

    const button: HTMLButtonElement = fixture.nativeElement.querySelector('.p-button');
    button.click();

    expect(layoutService.openSidebar).toHaveBeenCalled();
  });
});
