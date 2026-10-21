import { Injectable, signal } from '@angular/core';

/**
 * Holds the application-wide error message displayed in the banner at the top of every page.
 */
@Injectable({ providedIn: 'root' })
export class NotificationService {
  private readonly current = signal<string | null>(null);

  /** The message to display, or null when there is none. */
  readonly message = this.current.asReadonly();

  show(message: string): void {
    this.current.set(message);
  }

  clear(): void {
    this.current.set(null);
  }
}
