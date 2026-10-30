import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, Validators } from '@angular/forms';

import { accountErrorMessage, emailValidators, usernameValidators } from './account.validators';

describe('account validators', () => {
  const isValid = (value: string, validators: typeof usernameValidators) =>
    new FormControl(value, Validators.compose(validators)).valid;

  it('should accept a username of 3 to 30 letters, digits, dots, hyphens or underscores', () => {
    expect(isValid('alice.dev_42-x', usernameValidators)).toBeTrue();
    expect(isValid('al', usernameValidators)).toBeFalse();
    expect(isValid('a'.repeat(31), usernameValidators)).toBeFalse();
    expect(isValid('alice@home', usernameValidators)).toBeFalse();
  });

  it('should accept a valid email of at most 254 characters', () => {
    expect(isValid('alice@mdd.dev', emailValidators)).toBeTrue();
    expect(isValid('not-an-email', emailValidators)).toBeFalse();
    expect(isValid('', emailValidators)).toBeFalse();
  });

  it('should name the field already used when the API answers 409', () => {
    const conflict = (field: string) =>
      accountErrorMessage(new HttpErrorResponse({ status: 409, error: { errors: { [field]: 'used' } } }));

    expect(conflict('email')).toContain('adresse e-mail');
    expect(conflict('username')).toContain("nom d'utilisateur");
    expect(accountErrorMessage(new HttpErrorResponse({ status: 409, error: null }))).toContain('existe déjà');
  });

  it('should explain invalid fields and server failures', () => {
    expect(accountErrorMessage(new HttpErrorResponse({ status: 400 }))).toContain('invalides');
    expect(accountErrorMessage(new HttpErrorResponse({ status: 500 }))).toContain('Une erreur est survenue');
  });
});
