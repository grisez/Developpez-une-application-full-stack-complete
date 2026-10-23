import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Observable, of } from 'rxjs';

import { Profile as ProfileModel } from '../../core/models/profile';
import { AuthService } from '../../core/services/auth.service';
import { ProfileService } from '../../core/services/profile.service';
import { Profile } from './profile';

describe('Profile', () => {
  function render(profile: Observable<ProfileModel>): HTMLElement {
    TestBed.configureTestingModule({
      imports: [Profile],
      providers: [
        provideRouter([]),
        { provide: ProfileService, useValue: { getProfile: () => profile } },
        { provide: AuthService, useValue: { isLoggedIn: signal(true) } },
      ],
    });
    const fixture = TestBed.createComponent(Profile);
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  it('should display the user information and subscriptions', () => {
    // Act
    const element = render(
      of({
        id: 1,
        username: 'alice',
        email: 'alice@mdd.dev',
        subscriptions: [{ id: 1, name: 'Java', description: 'The JVM ecosystem', subscribed: true }],
      }),
    );

    // Assert
    expect(element.querySelector('dl')?.textContent).toContain('alice');
    expect(element.querySelector('dl')?.textContent).toContain('alice@mdd.dev');
    expect(element.querySelector('main li h3')?.textContent).toContain('Java');
  });

  it('should invite the user to discover the topics when they have no subscription', () => {
    // Act
    const element = render(of({ id: 1, username: 'alice', email: 'alice@mdd.dev', subscriptions: [] }));

    // Assert
    expect(element.querySelector('main a[href="/topics"]')).not.toBeNull();
  });
});
