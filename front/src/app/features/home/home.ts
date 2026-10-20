import { ChangeDetectionStrategy, Component } from '@angular/core';

/**
 * Landing page shown to visitors who are not logged in.
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Home {}
