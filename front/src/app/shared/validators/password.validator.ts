import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';

/** Same rule as the back-end: a digit, a lowercase letter, an uppercase letter and a special character. */
const PASSWORD_PATTERN = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^A-Za-z0-9]).*$/;

/**
 * Validates the password rule of the specifications: 8 to 64 characters with a digit, a lowercase letter,
 * an uppercase letter and a special character. An empty value is left to the required validator.
 */
export const passwordValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const value: string = control.value ?? '';
  if (value === '') {
    return null;
  }
  const valid = value.length >= 8 && value.length <= 64 && PASSWORD_PATTERN.test(value);
  return valid ? null : { password: true };
};
