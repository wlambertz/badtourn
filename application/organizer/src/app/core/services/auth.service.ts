import { Injectable, computed, signal } from '@angular/core';

interface OrganizerUser {
  username: string;
}

interface AuthState {
  user: OrganizerUser | null;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly storageKey = 'rallyon.organizer.session';
  private readonly state = signal<AuthState>({
    user: this.readStoredSession(),
  });

  readonly user = computed(() => this.state().user);
  readonly isAuthenticated = computed(() => this.user() !== null);

  login(username: string, password: string): boolean {
    const trimmedUsername = username.trim();
    const trimmedPassword = password.trim();
    const isValid = trimmedUsername === 'organizer' && trimmedPassword === 'rallyon';

    if (isValid) {
      const user: OrganizerUser = { username: trimmedUsername };
      this.persistSession(user);
      this.state.set({ user });
      return true;
    }

    this.logout();
    return false;
  }

  logout(): void {
    localStorage.removeItem(this.storageKey);
    this.state.set({ user: null });
  }

  private readStoredSession(): OrganizerUser | null {
    try {
      const raw = localStorage.getItem(this.storageKey);
      return raw ? (JSON.parse(raw) as OrganizerUser) : null;
    } catch {
      return null;
    }
  }

  private persistSession(user: OrganizerUser): void {
    localStorage.setItem(this.storageKey, JSON.stringify(user));
  }
}
