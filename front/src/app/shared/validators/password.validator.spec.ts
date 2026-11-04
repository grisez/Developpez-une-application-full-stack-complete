import { FormControl } from '@angular/forms';

import { passwordValidator } from './password.validator';

describe('passwordValidator', () => {
  const validate = (value: string) => passwordValidator(new FormControl(value));

  it('should accept a password following every rule', () => {
    // Act
    const result = validate('Str0ng!Password');

    // Assert
    expect(result).toBeNull();
  });

  it('should leave an empty value to the required validator', () => {
    // Act
    const result = validate('');

    // Assert
    expect(result).toBeNull();
  });

  it('should reject a password missing a rule', () => {
    // Arrange
    const passwords = ['Sh0rt!', 'nouppercase1!', 'NOLOWERCASE1!', 'NoDigitHere!', 'NoSpecial123'];

    // Act
    const results = passwords.map(validate);

    // Assert
    results.forEach((result) => expect(result).toEqual({ password: true }));
  });

  it('should reject a password longer than 64 characters', () => {
    // Act
    const result = validate('Aa1!' + 'a'.repeat(61));

    // Assert
    expect(result).toEqual({ password: true });
  });
});
