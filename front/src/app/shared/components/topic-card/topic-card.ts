import { ChangeDetectionStrategy, Component, input } from '@angular/core';

import { Topic } from '../../../core/models/topic';

/**
 * Card showing a topic, used on the topics page and on the profile page.
 * The action button (subscribe or unsubscribe) is projected by the page.
 */
@Component({
  selector: 'app-topic-card',
  templateUrl: './topic-card.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: { class: 'flex flex-col gap-3 rounded-lg bg-gray-100 p-4' },
})
export class TopicCard {
  readonly topic = input.required<Topic>();

  /** Level of the topic title, so that it follows the heading hierarchy of the page that hosts the card. */
  readonly headingLevel = input<2 | 3>(2);
}
