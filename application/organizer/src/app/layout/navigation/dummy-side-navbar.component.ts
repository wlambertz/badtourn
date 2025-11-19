import { CommonModule } from '@angular/common'
import { Component, EventEmitter, Input, Output, computed, signal } from '@angular/core'
import { RouterLink, RouterLinkActive } from '@angular/router'

import {
  SideNavAction,
  SideNavItem,
  SideNavProfile,
  SideNavSection,
} from './side-navbar.model'

@Component({
  selector: 'ro-dummy-side-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './dummy-side-navbar.component.html',
  styleUrl: './dummy-side-navbar.component.scss',
})
export class DummySideNavbarComponent {
  private readonly avatarText = signal('--')

  @Input() sections: SideNavSection[] = []
  @Input() quickAction?: SideNavAction
  @Input()
  get profile(): SideNavProfile | undefined {
    return this._profile
  }
  set profile(value: SideNavProfile | undefined) {
    this._profile = value
    this.avatarText.set(this.buildInitials(value?.name))
  }
  private _profile?: SideNavProfile

  @Input() collapsed = false

  @Output() linkSelected = new EventEmitter<SideNavItem>()
  @Output() actionSelected = new EventEmitter<SideNavAction>()
  @Output() logout = new EventEmitter<void>()
  @Output() collapsedChange = new EventEmitter<boolean>()

  readonly currentYear = new Date().getFullYear()
  readonly initials = computed(() => this.avatarText())

  onLinkSelect(item: SideNavItem): void {
    this.linkSelected.emit(item)
  }

  onActionSelect(action: SideNavAction): void {
    this.actionSelected.emit(action)
  }

  requestLogout(): void {
    this.logout.emit()
  }

  onCollapseToggle(): void {
    this.collapsedChange.emit(!this.collapsed)
  }

  private buildInitials(name?: string): string {
    if (!name) {
      return 'RO'
    }
    return name
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map((part) => part[0]?.toUpperCase())
      .join('') || 'RO'
  }
}
