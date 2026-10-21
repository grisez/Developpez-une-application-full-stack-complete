import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { NotificationBanner } from './shared/components/notification-banner/notification-banner';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NotificationBanner],
  template: '<app-notification-banner /><router-outlet />',
})
export class App {}
