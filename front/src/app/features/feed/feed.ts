import { ChangeDetectionStrategy, Component } from '@angular/core';

import { Header } from '../../shared/components/header/header';

/**
 * News feed of the logged-in user. The posts themselves are added by the news feed feature.
 */
@Component({
  selector: 'app-feed',
  imports: [Header],
  templateUrl: './feed.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Feed {}
