import { Injectable, computed, signal } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class LayoutService {
  private readonly sidebarState = signal(false);

  readonly sidebarVisible = computed(() => this.sidebarState());

  openSidebar(): void {
    this.sidebarState.set(true);
  }

  closeSidebar(): void {
    this.sidebarState.set(false);
  }

  toggleSidebar(): void {
    this.sidebarState.update((visible) => !visible);
  }
}
