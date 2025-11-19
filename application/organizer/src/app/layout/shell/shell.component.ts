import { CommonModule } from '@angular/common'
import { Component, inject } from '@angular/core'
import { Router, RouterOutlet } from '@angular/router'

import { AuthService } from '../../core/services/auth.service'
import { LayoutService } from '../../core/services/layout.service'
import { NAV_PROFILE, NAV_QUICK_ACTION, NAV_SECTIONS } from '../navigation/navigation.config'
import { DummySideNavbarComponent } from '../navigation/dummy-side-navbar.component'

@Component({
  selector: 'ro-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, DummySideNavbarComponent],
  templateUrl: './shell.component.html',
  styleUrl: './shell.component.scss',
})
export class ShellComponent {
  private readonly authService = inject(AuthService)
  private readonly router = inject(Router)
  private readonly layoutService = inject(LayoutService)

  readonly sections = NAV_SECTIONS
  readonly quickAction = NAV_QUICK_ACTION
  readonly profile = NAV_PROFILE
  readonly navCollapsed = this.layoutService.navCollapsed

  onLogout(): void {
    this.authService.logout()
    this.router.navigateByUrl('/login')
  }

  onCollapsedChange(collapsed: boolean): void {
    this.layoutService.setCollapsed(collapsed)
  }
}
