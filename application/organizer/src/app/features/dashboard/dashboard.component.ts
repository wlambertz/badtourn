import { CommonModule } from '@angular/common'
import { Component, computed, inject } from '@angular/core'
import { Router } from '@angular/router'
import { ButtonModule } from 'primeng/button'
import { CardModule } from 'primeng/card'
import { DividerModule } from 'primeng/divider'
import { TagModule } from 'primeng/tag'

import { DashboardService, QuickAction, UpcomingEvent } from './dashboard.service'

@Component({
  selector: 'ro-dashboard',
  standalone: true,
  imports: [CommonModule, ButtonModule, CardModule, DividerModule, TagModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export class DashboardComponent {
  private readonly dashboardService = inject(DashboardService)
  private readonly router = inject(Router)

  protected readonly summary = this.dashboardService.summary
  protected readonly upcomingEvent = computed<UpcomingEvent>(() => this.summary().upcomingEvent)
  protected readonly quickActions = computed<QuickAction[]>(() => this.summary().quickActions)

  onActionSelect(action: QuickAction): void {
    this.router.navigateByUrl(action.route)
  }
}
