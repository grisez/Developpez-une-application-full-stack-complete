import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import {
  ActivatedRouteSnapshot,
  GuardResult,
  MaybeAsync,
  RouterStateSnapshot,
  UrlTree,
  provideRouter,
} from '@angular/router';

import { AuthService } from '../services/auth.service';
import { authGuard, guestGuard } from './auth.guards';

describe('auth guards', () => {
  const loggedIn = signal(false);

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: AuthService, useValue: { isLoggedIn: loggedIn } }],
    });
  });

  function run(guard: typeof authGuard): MaybeAsync<GuardResult> {
    return TestBed.runInInjectionContext(() => guard({} as ActivatedRouteSnapshot, {} as RouterStateSnapshot));
  }

  function redirection(result: MaybeAsync<GuardResult>): string {
    return (result as UrlTree).toString();
  }

  it('should let a logged-in user open a protected page', () => {
    // Arrange
    loggedIn.set(true);

    // Act
    const result = run(authGuard);

    // Assert
    expect(result).toBeTrue();
  });

  it('should send a visitor from a protected page to the login page', () => {
    // Arrange
    loggedIn.set(false);

    // Act
    const result = run(authGuard);

    // Assert
    expect(redirection(result)).toBe('/login');
  });

  it('should let a visitor open the home, login and registration pages', () => {
    // Arrange
    loggedIn.set(false);

    // Act
    const result = run(guestGuard);

    // Assert
    expect(result).toBeTrue();
  });

  it('should send a logged-in user from those pages straight to the feed', () => {
    // Arrange
    loggedIn.set(true);

    // Act
    const result = run(guestGuard);

    // Assert
    expect(redirection(result)).toBe('/feed');
  });
});
