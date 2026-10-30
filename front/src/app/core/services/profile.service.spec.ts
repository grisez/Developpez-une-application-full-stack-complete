import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { Profile } from '../models/profile';
import { ProfileService } from './profile.service';

describe('ProfileService', () => {
  it('should fetch the profile of the logged-in user', () => {
    // Arrange
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const http = TestBed.inject(HttpTestingController);
    const profile: Profile = { id: 1, username: 'alice', email: 'alice@mdd.dev', subscriptions: [] };
    let received: Profile | undefined;

    // Act
    TestBed.inject(ProfileService)
      .getProfile()
      .subscribe((result) => (received = result));
    http.expectOne('/api/me').flush(profile);

    // Assert
    expect(received).toEqual(profile);
    http.verify();
  });

  it('should send the profile changes with PUT', () => {
    // Arrange
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const http = TestBed.inject(HttpTestingController);
    const changes = { username: 'alicia', email: 'alicia@mdd.dev', password: '' };

    // Act
    TestBed.inject(ProfileService).updateProfile(changes).subscribe();
    const request = http.expectOne('/api/me');
    request.flush({ id: 1, username: 'alicia', email: 'alicia@mdd.dev', subscriptions: [] });

    // Assert
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(changes);
    http.verify();
  });
});
