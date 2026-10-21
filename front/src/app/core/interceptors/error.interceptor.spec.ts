import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { AuthService } from '../services/auth.service';
import { NotificationService } from '../services/notification.service';
import { errorInterceptor } from './error.interceptor';

describe('errorInterceptor', () => {
  let http: HttpClient;
  let httpTesting: HttpTestingController;
  let authService: jasmine.SpyObj<AuthService>;
  let router: jasmine.SpyObj<Router>;
  let notifications: NotificationService;

  beforeEach(() => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['clearSession']);
    router = jasmine.createSpyObj<Router>('Router', ['navigateByUrl']);
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([errorInterceptor])),
        provideHttpClientTesting(),
        { provide: AuthService, useValue: authService },
        { provide: Router, useValue: router },
      ],
    });
    http = TestBed.inject(HttpClient);
    httpTesting = TestBed.inject(HttpTestingController);
    notifications = TestBed.inject(NotificationService);
  });

  afterEach(() => httpTesting.verify());

  function failWith(url: string, status: number): void {
    http.get(url).subscribe({ error: () => undefined });
    httpTesting.expectOne(url).flush(null, { status, statusText: 'Error' });
  }

  it('should clear the session and open the login page when the session has expired', () => {
    // Act
    failWith('/api/topics', 401);

    // Assert
    expect(authService.clearSession).toHaveBeenCalled();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/login');
  });

  it('should leave the expected 401 of the session check and of the login to their caller', () => {
    // Act
    failWith('/api/auth/me', 401);
    failWith('/api/auth/login', 401);

    // Assert
    expect(authService.clearSession).not.toHaveBeenCalled();
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  it('should display a banner when the server fails', () => {
    // Act
    failWith('/api/topics', 500);

    // Assert
    expect(notifications.message()).toContain('Une erreur est survenue');
  });

  it('should leave a business error such as a conflict to the component', () => {
    // Act
    failWith('/api/auth/register', 409);

    // Assert
    expect(notifications.message()).toBeNull();
    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });
});
