import { TestBed } from '@angular/core/testing';

import { NotificationService } from '../../../core/services/notification.service';
import { NotificationBanner } from './notification-banner';

describe('NotificationBanner', () => {
  it('should display the current message and hide it when closed', () => {
    // Arrange
    const fixture = TestBed.createComponent(NotificationBanner);
    const element = fixture.nativeElement as HTMLElement;
    TestBed.inject(NotificationService).show('Le serveur est injoignable.');
    fixture.detectChanges();
    expect(element.querySelector('[role="alert"]')?.textContent).toContain('Le serveur est injoignable.');

    // Act
    element.querySelector('button')!.click();
    fixture.detectChanges();

    // Assert
    expect(element.querySelector('[role="alert"]')).toBeNull();
  });
});
