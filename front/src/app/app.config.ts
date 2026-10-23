import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import {
  ApplicationConfig,
  LOCALE_ID,
  inject,
  provideAppInitializer,
  provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideRouter, withComponentInputBinding } from '@angular/router';

import { routes } from './app.routes';
import { errorInterceptor } from './core/interceptors/error.interceptor';
import { AuthService } from './core/services/auth.service';

// Dates are displayed in French, for example "23 octobre 2026"
registerLocaleData(localeFr);

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes, withComponentInputBinding()),
    // The XSRF protection of HttpClient sends the XSRF-TOKEN cookie back in the X-XSRF-TOKEN header
    provideHttpClient(withFetch(), withInterceptors([errorInterceptor])),
    provideAppInitializer(() => inject(AuthService).loadCurrentUser()),
    { provide: LOCALE_ID, useValue: 'fr-FR' },
  ],
};
