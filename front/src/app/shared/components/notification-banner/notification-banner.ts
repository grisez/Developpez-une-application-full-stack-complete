import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NavigationStart, Router } from '@angular/router';
import { filter } from 'rxjs';

import { NotificationService } from '../../../core/services/notification.service';

/**
 * Application-wide error banner, announced to screen readers as soon as a message appears.
 */
@Component({
  selector: 'app-notification-banner',
  templateUrl: './notification-banner.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotificationBanner {
  protected readonly notifications = inject(NotificationService);

  constructor() {
    // A message describes the page where the error happened: it would be misleading on the next page
    inject(Router)
      .events.pipe(
        filter((event) => event instanceof NavigationStart),
        takeUntilDestroyed(),
      )
      .subscribe(() => this.notifications.clear());
  }
}
