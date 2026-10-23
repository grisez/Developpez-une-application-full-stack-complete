import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';
import { Header } from './header';

describe('Header', () => {
  const loggedIn = signal(false);
  let authService: { isLoggedIn: typeof loggedIn; logout: jasmine.Spy };

  beforeEach(async () => {
    authService = { isLoggedIn: loggedIn, logout: jasmine.createSpy('logout').and.returnValue(of(undefined)) };
    await TestBed.configureTestingModule({
      imports: [Header],
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }],
    }).compileComponents();
  });

  function render(): { fixture: ComponentFixture<Header>; element: HTMLElement } {
    const fixture = TestBed.createComponent(Header);
    fixture.detectChanges();
    return { fixture, element: fixture.nativeElement as HTMLElement };
  }

  const burger = (element: HTMLElement) => element.querySelector<HTMLButtonElement>('[aria-controls="mobile-menu"]')!;

  it('should not offer any navigation to a visitor', () => {
    loggedIn.set(false);
    expect(render().element.querySelector('nav')).toBeNull();
  });

  it('should give a logged-in user access to the articles and the topics', () => {
    // Arrange
    loggedIn.set(true);

    // Act
    const links = Array.from(render().element.querySelectorAll('nav a')).map((link) => link.getAttribute('href'));

    // Assert
    expect(links).toEqual(['/feed', '/topics']);
  });

  it('should log the user out and open the home page', () => {
    // Arrange
    loggedIn.set(true);
    const navigate = spyOn(TestBed.inject(Router), 'navigateByUrl');
    const logoutButton = Array.from(render().element.querySelectorAll('button')).find((button) =>
      button.textContent?.includes('Se déconnecter'),
    )!;

    // Act
    logoutButton.click();

    // Assert
    expect(authService.logout).toHaveBeenCalled();
    expect(navigate).toHaveBeenCalledWith('/');
  });

  it('should open and close the mobile menu with the burger button', () => {
    // Arrange
    loggedIn.set(true);
    const { fixture, element } = render();

    // Act
    burger(element).click();
    fixture.detectChanges();

    // Assert
    expect(burger(element).getAttribute('aria-expanded')).toBe('true');
    expect(element.querySelector('#mobile-menu')).not.toBeNull();

    // Act
    burger(element).click();
    fixture.detectChanges();

    // Assert
    expect(element.querySelector('#mobile-menu')).toBeNull();
  });

  it('should close the mobile menu with the Escape key', () => {
    // Arrange
    loggedIn.set(true);
    const { fixture, element } = render();
    burger(element).click();
    fixture.detectChanges();

    // Act
    document.dispatchEvent(new KeyboardEvent('keydown', { key: 'Escape' }));
    fixture.detectChanges();

    // Assert
    expect(element.querySelector('#mobile-menu')).toBeNull();
  });
});
