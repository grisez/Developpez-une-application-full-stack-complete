import { AbstractControl } from '@angular/forms';

/** Whether a field should display its error: invalid and already visited by the user. */
export function showError(control: AbstractControl): boolean {
  return control.invalid && control.touched;
}
