import { Injectable, computed, signal } from '@angular/core'

@Injectable({
  providedIn: 'root',
})
export class LayoutService {
  private readonly storageKey = 'rallyon.organizer.navCollapsed'
  private readonly collapsedState = signal(this.readInitialState())

  readonly navCollapsed = computed(() => this.collapsedState())

  setCollapsed(collapsed: boolean): void {
    this.collapsedState.set(collapsed)
    this.persistState(collapsed)
  }

  toggleCollapsed(): void {
    this.setCollapsed(!this.collapsedState())
  }

  private readInitialState(): boolean {
    try {
      return localStorage.getItem(this.storageKey) === 'true'
    } catch {
      return false
    }
  }

  private persistState(collapsed: boolean): void {
    try {
      localStorage.setItem(this.storageKey, String(collapsed))
    } catch {
      // ignore persistence failures
    }
  }
}
