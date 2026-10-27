import { TestBed } from '@angular/core/testing';

import { NotificationService } from './notification.service';

describe('NotificationService', () => {
  it('should hold the current message until it is cleared', () => {
    // Arrange
    const notifications = TestBed.inject(NotificationService);

    // Act
    notifications.show('Le serveur est injoignable.');
    const shown = notifications.message();
    notifications.clear();

    // Assert
    expect(shown).toBe('Le serveur est injoignable.');
    expect(notifications.message()).toBeNull();
  });
});
