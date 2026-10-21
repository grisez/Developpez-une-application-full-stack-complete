import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, catchError, map, of, tap } from 'rxjs';

import { LoginRequest, RegisterRequest, User } from '../models/user';

/**
 * Holds the logged-in user and talks to the authentication endpoints.
 * The JWT itself stays in an HttpOnly cookie handled by the browser: this service never sees it.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly user = signal<User | null>(null);

  /** The logged-in user, or null for a visitor. */
  readonly currentUser = this.user.asReadonly();

  readonly isLoggedIn = computed(() => this.user() !== null);

  /**
   * Asks the API who is logged in. Called once at startup: it restores the session and gives the browser
   * the XSRF-TOKEN cookie that the first POST request needs.
   */
  loadCurrentUser(): Observable<void> {
    return this.http.get<User>('/api/auth/me').pipe(
      tap((user) => this.user.set(user)),
      catchError(() => {
        this.user.set(null);
        return of(undefined);
      }),
      map(() => undefined),
    );
  }

  /** Creates an account; the API logs the new user in by setting the authentication cookie. */
  register(request: RegisterRequest): Observable<User> {
    return this.http.post<User>('/api/auth/register', request).pipe(tap((user) => this.user.set(user)));
  }

  /** Logs in with an email or a username; the API sets the authentication cookie. */
  login(request: LoginRequest): Observable<User> {
    return this.http.post<User>('/api/auth/login', request).pipe(tap((user) => this.user.set(user)));
  }

  /** Forgets the user locally, for instance when the API reports that the session has expired. */
  clearSession(): void {
    this.user.set(null);
  }
}
