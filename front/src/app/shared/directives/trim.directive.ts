import { Directive, inject } from '@angular/core';
import { NgControl } from '@angular/forms';

/**
 * Removes the surrounding spaces of a form field when the user leaves it or presses Enter, so that an
 * invisible trailing space is never reported as an invalid email or username.
 * Never put it on a password field: a space is a valid password character.
 */
@Directive({
  selector: 'input[appTrim]',
  host: {
    '(blur)': 'trim()',
    '(keydown.enter)': 'trim()',
  },
})
export class TrimDirective {
  private readonly ngControl = inject(NgControl, { self: true });

  protected trim(): void {
    const value: unknown = this.ngControl.value;
    if (typeof value === 'string' && value !== value.trim()) {
      this.ngControl.control?.setValue(value.trim());
    }
  }
}
