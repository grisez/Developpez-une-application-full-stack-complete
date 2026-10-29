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
    loggedIn.set(true);
    expect(run(authGuard)).toBeTrue();
  });

  it('should send a visitor from a protected page to the login page', () => {
    loggedIn.set(false);
    expect(redirection(run(authGuard))).toBe('/login');
  });

  it('should let a visitor open the home, login and registration pages', () => {
    loggedIn.set(false);
    expect(run(guestGuard)).toBeTrue();
  });

  it('should send a logged-in user from those pages straight to the feed', () => {
    loggedIn.set(true);
    expect(redirection(run(guestGuard))).toBe('/feed');
  });
});
