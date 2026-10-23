import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { Profile, UpdateProfileRequest } from '../models/profile';

/**
 * Talks to the profile endpoints of the logged-in user.
 */
@Injectable({ providedIn: 'root' })
export class ProfileService {
  private readonly http = inject(HttpClient);

  /** The profile of the logged-in user, with their subscriptions. */
  getProfile(): Observable<Profile> {
    return this.http.get<Profile>('/api/me');
  }

  /** Saves the email, the username and, when filled in, the new password. */
  updateProfile(request: UpdateProfileRequest): Observable<Profile> {
    return this.http.put<Profile>('/api/me', request);
  }
}
