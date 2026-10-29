import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';

import { NotificationService } from '../../../core/services/notification.service';
import { NotificationBanner } from './notification-banner';

describe('NotificationBanner', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({ providers: [provideRouter([])] });
  });

  function renderWithMessage(): { fixture: ComponentFixture<NotificationBanner>; element: HTMLElement } {
    const fixture = TestBed.createComponent(NotificationBanner);
    TestBed.inject(NotificationService).show('Le serveur est injoignable.');
    fixture.detectChanges();
    return { fixture, element: fixture.nativeElement as HTMLElement };
  }

  it('should display the current message and hide it when closed', () => {
    // Arrange
    const { fixture, element } = renderWithMessage();
    expect(element.querySelector('[role="alert"]')?.textContent).toContain('Le serveur est injoignable.');

    // Act
    element.querySelector('button')!.click();
    fixture.detectChanges();

    // Assert
    expect(element.querySelector('[role="alert"]')).toBeNull();
  });

  it('should hide the message when the user goes to another page', async () => {
    // Arrange
    const { fixture, element } = renderWithMessage();

    // Act
    await TestBed.inject(Router).navigateByUrl('/');
    fixture.detectChanges();

    // Assert
    expect(element.querySelector('[role="alert"]')).toBeNull();
  });
});
