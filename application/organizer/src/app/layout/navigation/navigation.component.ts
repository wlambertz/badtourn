import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { ButtonModule } from 'primeng/button';
import { DrawerModule } from 'primeng/drawer';

import { AuthService } from '../../core/services/auth.service';
import { LayoutService } from '../../core/services/layout.service';

interface NavItem {
  label: string;
  icon: string;
  route: string;
}

@Component({
  selector: 'app-navigation',
  standalone: true,
  imports: [CommonModule, DrawerModule, ButtonModule, RouterLink, RouterLinkActive],
  templateUrl: './navigation.component.html',
  styleUrl: './navigation.component.scss',
})
export class NavigationComponent {
  private readonly layoutService = inject(LayoutService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  readonly sidebarVisible = this.layoutService.sidebarVisible;

  readonly items: NavItem[] = [
    { label: 'Home', icon: 'pi pi-home', route: '/dashboard' },
    { label: 'Events', icon: 'pi pi-calendar', route: '/events' },
    { label: 'Settings', icon: 'pi pi-cog', route: '/settings' },
  ];

  hide(): void {
    this.layoutService.closeSidebar();
  }

  logout(): void {
    this.authService.logout();
    this.hide();
    this.router.navigateByUrl('/login');
  }
}
