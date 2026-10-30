import { ChangeDetectionStrategy, Component } from '@angular/core';
import { RouterLink } from '@angular/router';

/**
 * Page displayed for any unknown route.
 */
@Component({
  selector: 'app-not-found',
  imports: [RouterLink],
  templateUrl: './not-found.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotFound {}
