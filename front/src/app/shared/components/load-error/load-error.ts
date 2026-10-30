import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

/**
 * Message displayed in place of data that could not be loaded, with a button to try again.
 */
@Component({
  selector: 'app-load-error',
  templateUrl: './load-error.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoadError {
  readonly message = input.required<string>();

  readonly retry = output<void>();
}
