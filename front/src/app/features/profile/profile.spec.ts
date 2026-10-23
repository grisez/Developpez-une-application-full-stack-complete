import { HttpErrorResponse } from '@angular/common/http';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { Profile as ProfileModel } from '../../core/models/profile';
import { AuthService } from '../../core/services/auth.service';
import { ProfileService } from '../../core/services/profile.service';
import { Profile } from './profile';

describe('Profile', () => {
  const alice: ProfileModel = {
    id: 1,
    username: 'alice',
    email: 'alice@mdd.dev',
    subscriptions: [{ id: 1, name: 'Java', description: 'The JVM ecosystem', subscribed: true }],
  };
  let profileService: jasmine.SpyObj<ProfileService>;
  let authService: { isLoggedIn: ReturnType<typeof signal<boolean>>; updateCurrentUser: jasmine.Spy };

  beforeEach(() => {
    profileService = jasmine.createSpyObj<ProfileService>('ProfileService', ['getProfile', 'updateProfile']);
    authService = { isLoggedIn: signal(true), updateCurrentUser: jasmine.createSpy('updateCurrentUser') };
    TestBed.configureTestingModule({
      imports: [Profile],
      providers: [
        provideRouter([]),
        { provide: ProfileService, useValue: profileService },
        { provide: AuthService, useValue: authService },
      ],
    });
  });

  function render(profile: ProfileModel = alice): { fixture: ComponentFixture<Profile>; element: HTMLElement } {
    profileService.getProfile.and.returnValue(of(profile));
    const fixture = TestBed.createComponent(Profile);
    fixture.detectChanges();
    return { fixture, element: fixture.nativeElement as HTMLElement };
  }

  const input = (element: HTMLElement, id: string) => element.querySelector<HTMLInputElement>(`#${id}`)!;

  function submit(fixture: ComponentFixture<Profile>, element: HTMLElement): void {
    element.querySelector('form')!.dispatchEvent(new Event('submit'));
    fixture.detectChanges();
  }

  it('should fill the form with the user information, leave the password empty and list the subscriptions', () => {
    // Act
    const { element } = render();

    // Assert
    expect(input(element, 'username').value).toBe('alice');
    expect(input(element, 'email').value).toBe('alice@mdd.dev');
    expect(input(element, 'password').value).toBe('');
    expect(element.querySelector('main li h3')?.textContent).toContain('Java');
  });

  it('should save the changes and confirm it', () => {
    // Arrange
    const updated = { ...alice, username: 'alicia' };
    profileService.updateProfile.and.returnValue(of(updated));
    const { fixture, element } = render();
    const username = input(element, 'username');
    username.value = 'alicia';
    username.dispatchEvent(new Event('input'));

    // Act
    submit(fixture, element);

    // Assert
    expect(profileService.updateProfile).toHaveBeenCalledWith({
      username: 'alicia',
      email: 'alice@mdd.dev',
      password: '',
    });
    expect(authService.updateCurrentUser).toHaveBeenCalledWith({ id: 1, username: 'alicia', email: 'alice@mdd.dev' });
    expect(element.querySelector('[role="status"]')?.textContent).toContain('mis à jour');
  });

  it('should explain that the username is taken when the API answers 409', () => {
    // Arrange
    profileService.updateProfile.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 409, error: { errors: { username: 'already used' } } })),
    );
    const { fixture, element } = render();

    // Act
    submit(fixture, element);

    // Assert
    expect(element.querySelector('[role="alert"]')?.textContent).toContain("nom d'utilisateur est déjà utilisé");
  });

  it('should not save a new password that breaks the rule', () => {
    // Arrange
    const { fixture, element } = render();
    const password = input(element, 'password');
    password.value = 'weak';
    password.dispatchEvent(new Event('input'));

    // Act
    submit(fixture, element);

    // Assert
    expect(profileService.updateProfile).not.toHaveBeenCalled();
  });

  it('should invite the user to discover the topics when they have no subscription', () => {
    // Act
    const { element } = render({ ...alice, subscriptions: [] });

    // Assert
    expect(element.querySelector('main a[href="/topics"]')).not.toBeNull();
  });
});
