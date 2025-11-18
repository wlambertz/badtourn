import { Routes } from '@angular/router'
import { loginRedirectGuard } from './core/guards/login.guard'
import { authChildGuard, authGuard } from './core/guards/auth.guard'
import { LoginComponent } from './features/auth/login.component'
import { DashboardComponent } from './features/dashboard/dashboard.component'
import { EventsComponent } from './features/events/events.component'
import { SettingsComponent } from './features/settings/settings.component'
import { ShellComponent } from './layout/shell/shell.component'

export const routes: Routes = [
  {
    path: 'login',
    canActivate: [loginRedirectGuard],
    component: LoginComponent,
  },
  {
    path: '',
    component: ShellComponent,
    canActivate: [authGuard],
    canActivateChild: [authChildGuard],
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'dashboard' },
      { path: 'dashboard', component: DashboardComponent },
      { path: 'events', component: EventsComponent },
      { path: 'settings', component: SettingsComponent },
    ],
  },
  { path: '**', redirectTo: '' },
]
