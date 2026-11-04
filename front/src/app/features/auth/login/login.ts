import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';
import { Header } from '../../../shared/components/header/header';
import { TrimDirective } from '../../../shared/directives/trim.directive';
import { showError } from '../../../shared/utils/form-errors';
import { GENERIC_ERROR_MESSAGE } from '../../../shared/utils/messages';

/**
 * Login form, accepting either the email or the username in a single field.
 */
@Component({
  selector: 'app-login',
  imports: [ReactiveFormsModule, RouterLink, Header, TrimDirective],
  templateUrl: './login.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Login {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly form = inject(NonNullableFormBuilder).group({
    identifier: ['', Validators.required],
    password: ['', Validators.required],
  });

  protected readonly showError = showError;

  protected readonly submitting = signal(false);

  protected readonly serverError = signal<string | null>(null);

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting.set(true);
    this.serverError.set(null);
    this.authService.login(this.form.getRawValue()).subscribe({
      next: () => this.router.navigateByUrl('/feed'),
      error: (error: HttpErrorResponse) => {
        this.submitting.set(false);
        this.serverError.set(error.status === 401 ? 'Identifiant ou mot de passe incorrect.' : GENERIC_ERROR_MESSAGE);
      },
    });
  }
}
