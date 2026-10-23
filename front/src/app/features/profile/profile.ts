import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { ProfileService } from '../../core/services/profile.service';
import { Header } from '../../shared/components/header/header';
import { TopicCard } from '../../shared/components/topic-card/topic-card';

/**
 * Profile page of the logged-in user: their information and their subscriptions.
 */
@Component({
  selector: 'app-profile',
  imports: [Header, TopicCard, RouterLink],
  templateUrl: './profile.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Profile {
  /** Undefined while loading; an error is reported by the error interceptor. */
  protected readonly profile = toSignal(inject(ProfileService).getProfile());
}
