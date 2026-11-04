import { FormControl, Validators } from '@angular/forms';

import { showError } from './form-errors';

describe('showError', () => {
  it('should only show the error of an invalid field once the user has visited it', () => {
    // Arrange
    const control = new FormControl('', Validators.required);

    // Act
    const beforeVisit = showError(control);
    control.markAsTouched();
    const afterVisit = showError(control);

    // Assert
    expect(beforeVisit).toBeFalse();
    expect(afterVisit).toBeTrue();
  });

  it('should not show any error on a valid field', () => {
    // Arrange
    const control = new FormControl('MDD', Validators.required);
    control.markAsTouched();

    // Act
    const result = showError(control);

    // Assert
    expect(result).toBeFalse();
  });
});
