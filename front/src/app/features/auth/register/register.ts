import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { ProblemDetail } from '../../../core/models/problem-detail';
import { AuthService } from '../../../core/services/auth.service';
import { Header } from '../../../shared/components/header/header';
import { passwordValidator } from '../../../shared/validators/password.validator';

/** Messages shown when the API rejects a field already taken by another account. */
const CONFLICT_MESSAGES: Record<string, string> = {
  email: 'Cette adresse e-mail est déjà utilisée.',
  username: "Ce nom d'utilisateur est déjà utilisé.",
};

/**
 * Registration form. The rules mirror the back-end validation, which remains the reference.
 */
@Component({
  selector: 'app-register',
  imports: [ReactiveFormsModule, RouterLink, Header],
  templateUrl: './register.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Register {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly form = inject(NonNullableFormBuilder).group({
    username: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(30)]],
    email: ['', [Validators.required, Validators.email, Validators.maxLength(254)]],
    password: ['', [Validators.required, passwordValidator]],
  });

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
      next: () => this.router.navigateByUrl('/'),
      error: (error: HttpErrorResponse) => {
        this.submitting.set(false);
        this.serverError.set(this.messageFor(error));
      },
    });
  }

  /** Whether the field should display its error: invalid and already visited by the user. */
  protected showError(field: 'username' | 'email' | 'password'): boolean {
    const control = this.form.controls[field];
    return control.invalid && control.touched;
  }

  private messageFor(error: HttpErrorResponse): string {
    if (error.status === 409) {
      const problem = error.error as ProblemDetail | null;
      const field = Object.keys(problem?.errors ?? {})[0];
      return CONFLICT_MESSAGES[field] ?? 'Ce compte existe déjà.';
    }
    if (error.status === 400) {
      return 'Certains champs sont invalides. Vérifiez votre saisie.';
    }
    return 'Une erreur est survenue. Veuillez réessayer plus tard.';
  }
}
