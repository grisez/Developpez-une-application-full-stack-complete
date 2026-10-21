import { ChangeDetectionStrategy, Component, inject } from '@angular/core';

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
}
