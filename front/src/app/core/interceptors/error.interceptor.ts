import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';

import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';

/**
 * Requests for which a 401 is an expected answer handled where they are sent: the session check at startup
 * and a login with wrong credentials.
 */
const EXPECTED_UNAUTHORIZED_URLS = ['/api/auth/me', '/api/auth/login'];

/**
 * Handles in one place the HTTP errors that mean the same thing whatever the page:
 * an expired or invalid session, an unreachable server and a server error.
 * Errors tied to a form, such as a 409 on registration, are left to the component that sent the request.
 */
export const errorInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const notifications = inject(NotificationService);

  return next(request).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse) {
        if (error.status === 401 && !EXPECTED_UNAUTHORIZED_URLS.includes(request.url)) {
          authService.clearSession();
          router.navigateByUrl('/login');
        } else if (error.status === 0) {
          notifications.show('Le serveur est injoignable. Vérifiez votre connexion puis réessayez.');
        } else if (error.status >= 500) {
          notifications.show('Une erreur est survenue. Veuillez réessayer plus tard.');
        }
      }
      return throwError(() => error);
    }),
  );
};
