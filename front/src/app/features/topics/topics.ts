import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { TopicService } from '../../core/services/topic.service';
import { Header } from '../../shared/components/header/header';

/**
 * Page listing every topic, whether the user is subscribed to it or not.
 */
@Component({
  selector: 'app-topics',
  imports: [Header],
  templateUrl: './topics.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Topics {
  /** Undefined while loading; an error is reported by the error interceptor. */
  protected readonly topics = toSignal(inject(TopicService).getTopics());
}
