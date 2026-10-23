import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { Profile } from '../models/profile';

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
}
