import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from '../services/auth.service';

/*
 * Both guards rely on the session restored at startup by the application initializer,
 * which completes before the first navigation.
 */

/** Pages for logged-in users only: a visitor is sent to the login page. */
export const authGuard: CanActivateFn = () =>
  inject(AuthService).isLoggedIn() || inject(Router).createUrlTree(['/login']);

/** Pages for visitors only (home, login, registration): a logged-in user goes straight to their feed. */
export const guestGuard: CanActivateFn = () =>
  !inject(AuthService).isLoggedIn() || inject(Router).createUrlTree(['/feed']);
