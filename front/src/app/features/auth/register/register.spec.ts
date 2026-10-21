import { HttpErrorResponse } from '@angular/common/http';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';
import { Register } from './register';

describe('Register', () => {
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['register']);
    await TestBed.configureTestingModule({
      imports: [Register],
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }],
    }).compileComponents();
  });

  function render() {
    const fixture = TestBed.createComponent(Register);
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
    return { fixture, element, fill, submit };
  }

  it('should not call the API and should show the errors when the form is invalid', () => {
    // Arrange
    const { element, fill, submit } = render();
    fill('username', 'al');
    fill('email', 'not-an-email');
    fill('password', 'password');

    // Act
    submit();

    // Assert
    expect(authService.register).not.toHaveBeenCalled();
    expect(element.querySelector('#username-error')).not.toBeNull();
    expect(element.querySelector('#email-error')).not.toBeNull();
  });

  it('should register with valid data and open the feed', () => {
    // Arrange
    authService.register.and.returnValue(of({ id: 1, username: 'alice', email: 'alice@mdd.dev' }));
    const navigate = spyOn(TestBed.inject(Router), 'navigateByUrl');
    const { fill, submit } = render();
    fill('username', 'alice');
    fill('email', 'alice@mdd.dev');
    fill('password', 'Str0ng!Password');

    // Act
    submit();

    // Assert
    expect(authService.register).toHaveBeenCalledWith({
      username: 'alice',
      email: 'alice@mdd.dev',
      password: 'Str0ng!Password',
    });
    expect(navigate).toHaveBeenCalledWith('/feed');
  });

  it('should explain that the email is already used when the API answers 409', () => {
    // Arrange
    authService.register.and.returnValue(
      throwError(() => new HttpErrorResponse({ status: 409, error: { errors: { email: 'already used' } } })),
    );
    const { element, fill, submit } = render();
    fill('username', 'alice');
    fill('email', 'alice@mdd.dev');
    fill('password', 'Str0ng!Password');

    // Act
    submit();

    // Assert
    expect(element.querySelector('[role="alert"]')?.textContent).toContain('adresse e-mail est déjà utilisée');
  });
});
