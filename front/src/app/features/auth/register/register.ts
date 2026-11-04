import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';
import { Header } from '../../../shared/components/header/header';
import { TrimDirective } from '../../../shared/directives/trim.directive';
import {
  ACCOUNT_RULES,
  accountErrorMessage,
  emailValidators,
  usernameValidators,
} from '../../../shared/validators/account.validators';
import { passwordValidator } from '../../../shared/validators/password.validator';
import { showError } from '../../../shared/utils/form-errors';

/**
 * Registration form. The rules mirror the back-end validation, which remains the reference.
 */
@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink, Header, TrimDirective],
  templateUrl: './register.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Register {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly form = inject(NonNullableFormBuilder).group({
    username: ['', usernameValidators],
    email: ['', emailValidators],
    password: ['', [Validators.required, passwordValidator]],
  });

  protected readonly showError = showError;

  protected readonly rules = ACCOUNT_RULES;

  protected readonly submitting = signal(false);

  protected readonly serverError = signal<string | null>(null);

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting.set(true);
    this.serverError.set(null);
    this.authService.register(this.form.getRawValue()).subscribe({
      next: () => this.router.navigateByUrl('/feed'),
      error: (error: HttpErrorResponse) => {
        this.submitting.set(false);
        this.serverError.set(accountErrorMessage(error));
      },
    });
  }
}
