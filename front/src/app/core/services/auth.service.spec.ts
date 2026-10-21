import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { User } from '../models/user';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  const alice: User = { id: 1, username: 'alice', email: 'alice@mdd.dev' };
  let service: AuthService;
  let http: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    http = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should store the user once registered', () => {
    // Act
    service.register({ email: 'alice@mdd.dev', username: 'alice', password: 'Str0ng!Password' }).subscribe();
    const request = http.expectOne('/api/auth/register');
    request.flush(alice);

    // Assert
    expect(request.request.method).toBe('POST');
    expect(service.currentUser()).toEqual(alice);
    expect(service.isLoggedIn()).toBeTrue();
  });

  it('should store the user once logged in', () => {
    // Act
    service.login({ identifier: 'alice', password: 'Str0ng!Password' }).subscribe();
    const request = http.expectOne('/api/auth/login');
    request.flush(alice);

    // Assert
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ identifier: 'alice', password: 'Str0ng!Password' });
    expect(service.currentUser()).toEqual(alice);
  });

  it('should restore the logged-in user at startup', () => {
    // Act
    service.loadCurrentUser().subscribe();
    http.expectOne('/api/auth/me').flush(alice);

    // Assert
    expect(service.currentUser()).toEqual(alice);
  });

  it('should consider a visitor logged out when the session is missing', () => {
    // Act
    let completed = false;
    service.loadCurrentUser().subscribe({ complete: () => (completed = true) });
    http.expectOne('/api/auth/me').flush(null, { status: 401, statusText: 'Unauthorized' });

    // Assert
    expect(completed).toBeTrue();
    expect(service.isLoggedIn()).toBeFalse();
  });
});
