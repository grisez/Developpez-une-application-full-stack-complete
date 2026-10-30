import { HttpErrorResponse } from '@angular/common/http';
import { ValidatorFn, Validators } from '@angular/forms';

import { ProblemDetail } from '../../core/models/problem-detail';

/** Same rules as the back-end: 3 to 30 letters, digits, dots, hyphens or underscores. */
export const usernameValidators: ValidatorFn[] = [
  Validators.required,
  Validators.minLength(3),
  Validators.maxLength(30),
  Validators.pattern(/^[A-Za-z0-9._-]+$/),
];

/** Same rules as the back-end: a valid address of at most 254 characters. */
export const emailValidators: ValidatorFn[] = [Validators.required, Validators.email, Validators.maxLength(254)];

/** Messages shown when the API rejects a field already taken by another account. */
const CONFLICT_MESSAGES: Record<string, string> = {
  email: 'Cette adresse e-mail est déjà utilisée.',
  username: "Ce nom d'utilisateur est déjà utilisé.",
};

/**
 * Message to display when the API rejects the account form of the registration or of the profile page.
 */
export function accountErrorMessage(error: HttpErrorResponse): string {
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
