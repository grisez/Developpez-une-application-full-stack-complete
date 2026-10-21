import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';
import { Login } from './login';

describe('Login', () => {
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['login']);
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }],
    }).compileComponents();
  });

  function render() {
    const fixture = TestBed.createComponent(Login);
    fixture.detectChanges();
    const element = fixture.nativeElement as HTMLElement;
    const fill = (id: string, value: string) => {
      const input = element.querySelector<HTMLInputElement>(`#${id}`)!;
      input.value = value;
      input.dispatchEvent(new Event('input'));
      input.dispatchEvent(new Event('blur'));
    };
    const submit = () => {
      element.querySelector('form')!.dispatchEvent(new Event('submit'));
      fixture.detectChanges();
    };
    return { element, fill, submit };
  }

  it('should not call the API when a field is empty', () => {
    // Arrange
    const { element, submit } = render();

    // Act
    submit();

    // Assert
    expect(authService.login).not.toHaveBeenCalled();
    expect(element.querySelector('#identifier-error')).not.toBeNull();
    expect(element.querySelector('#password-error')).not.toBeNull();
  });

  it('should log in and open the feed', () => {
    // Arrange
    authService.login.and.returnValue(of({ id: 1, username: 'alice', email: 'alice@mdd.dev' }));
    const navigate = spyOn(TestBed.inject(Router), 'navigateByUrl');
    const { fill, submit } = render();
    fill('identifier', 'alice');
    fill('password', 'Str0ng!Password');

    // Act
    submit();

    // Assert
    expect(authService.login).toHaveBeenCalledWith({ identifier: 'alice', password: 'Str0ng!Password' });
    expect(navigate).toHaveBeenCalledWith('/feed');
  });

  it('should show a generic message when the credentials are rejected', () => {
    // Arrange
    authService.login.and.returnValue(throwError(() => new HttpErrorResponse({ status: 401 })));
    const { element, fill, submit } = render();
    fill('identifier', 'alice');
    fill('password', 'wrong');

    // Act
    submit();

    // Assert
    expect(element.querySelector('[role="alert"]')?.textContent).toContain('Identifiant ou mot de passe incorrect');
  });
});
