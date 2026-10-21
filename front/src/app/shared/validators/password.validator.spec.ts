import { FormControl } from '@angular/forms';

import { passwordValidator } from './password.validator';

describe('passwordValidator', () => {
  const validate = (value: string) => passwordValidator(new FormControl(value));

  it('should accept a password following every rule', () => {
    expect(validate('Str0ng!Password')).toBeNull();
  });

  it('should leave an empty value to the required validator', () => {
    expect(validate('')).toBeNull();
  });

  it('should reject a password missing a rule', () => {
    expect(validate('Sh0rt!')).toEqual({ password: true });
    expect(validate('nouppercase1!')).toEqual({ password: true });
    expect(validate('NOLOWERCASE1!')).toEqual({ password: true });
    expect(validate('NoDigitHere!')).toEqual({ password: true });
    expect(validate('NoSpecial123')).toEqual({ password: true });
  });

  it('should reject a password longer than 64 characters', () => {
    expect(validate('Aa1!' + 'a'.repeat(61))).toEqual({ password: true });
  });
});
