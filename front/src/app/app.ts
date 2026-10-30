import { DOCUMENT } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { NotificationBanner } from './shared/components/notification-banner/notification-banner';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NotificationBanner],
  templateUrl: './app.html',
})
export class App {
  private readonly document = inject(DOCUMENT);

  /** Moves the keyboard focus to the main content of the current page, whichever page it is. */
  protected skipToContent(event: Event): void {
    event.preventDefault();
    const main = this.document.querySelector('main');
    // A main element is not focusable by default: -1 allows focusing it from code without adding it to the Tab order
    main?.setAttribute('tabindex', '-1');
    main?.focus();
  }
}
