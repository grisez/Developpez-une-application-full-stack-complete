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
});
