import { HttpErrorResponse } from '@angular/common/http';
import { FormControl, Validators } from '@angular/forms';

import { accountErrorMessage, emailValidators, usernameValidators } from './account.validators';

describe('account validators', () => {
  const isValid = (value: string, validators: typeof usernameValidators) =>
    new FormControl(value, Validators.compose(validators)).valid;

  it('should accept a username of 3 to 30 letters, digits, dots, hyphens or underscores', () => {
    // Act
    const accepted = isValid('alice.dev_42-x', usernameValidators);
    const rejected = ['al', 'a'.repeat(31), 'alice@home'].map((value) => isValid(value, usernameValidators));

    // Assert
    expect(accepted).toBeTrue();
    expect(rejected).toEqual([false, false, false]);
  });

  it('should accept a valid email of at most 254 characters', () => {
    // Arrange
    const domain = (lastLabel: number) =>
      ['b', 'c'].map((letter) => letter.repeat(63)).join('.') + '.' + 'e'.repeat(lastLabel);
    const longest = 'a'.repeat(64) + '@' + domain(61);

    // Act
    const accepted = ['alice@mdd.dev', longest].map((value) => isValid(value, emailValidators));
    const rejected = ['not-an-email', '', 'a'.repeat(64) + '@' + domain(62)].map((value) =>
      isValid(value, emailValidators),
    );

    // Assert
    expect(accepted).toEqual([true, true]);
    expect(rejected).toEqual([false, false, false]);
  });

  it('should name the field already used when the API answers 409', () => {
    // Arrange
    const conflict = (errors: Record<string, string> | null) =>
      new HttpErrorResponse({ status: 409, error: errors && { errors } });

    // Act
    const [email, username, unknown] = [
      conflict({ email: 'used' }),
      conflict({ username: 'used' }),
      conflict(null),
    ].map(accountErrorMessage);

    // Assert
    expect(email).toContain('adresse e-mail');
    expect(username).toContain("nom d'utilisateur");
    expect(unknown).toContain('existe déjà');
  });

  it('should explain invalid fields and server failures', () => {
    // Act
    const [invalid, failure] = [400, 500].map((status) => accountErrorMessage(new HttpErrorResponse({ status })));

    // Assert
    expect(invalid).toContain('invalides');
    expect(failure).toContain('Une erreur est survenue');
  });
});
