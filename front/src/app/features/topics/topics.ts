import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { Topic } from '../../core/models/topic';
import { TopicService } from '../../core/services/topic.service';
import { Header } from '../../shared/components/header/header';
import { TopicCard } from '../../shared/components/topic-card/topic-card';

/**
 * Page listing every topic, whether the user is subscribed to it or not, with a button to subscribe.
 */
@Component({
  selector: 'app-topics',
  imports: [Header, TopicCard],
  templateUrl: './topics.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Topics {
  private readonly topicService = inject(TopicService);

  /** Undefined while loading; an error is reported by the error interceptor. */
  protected readonly topics = signal<Topic[] | undefined>(undefined);

  /** Topics whose subscription request is in progress, to prevent a double click. */
  protected readonly pending = signal<ReadonlySet<number>>(new Set());

  constructor() {
    this.topicService
      .getTopics()
      .pipe(takeUntilDestroyed())
      .subscribe((topics) => this.topics.set(topics));
  }

  protected subscribe(topic: Topic): void {
    this.setPending(topic.id, true);
    this.topicService.subscribe(topic.id).subscribe({
      next: () => {
        this.topics.update((topics) =>
          topics?.map((current) => (current.id === topic.id ? { ...current, subscribed: true } : current)),
        );
        this.setPending(topic.id, false);
      },
      error: () => this.setPending(topic.id, false),
    });
  }

  private setPending(topicId: number, isPending: boolean): void {
    this.pending.update((ids) => {
      const next = new Set(ids);
      if (isPending) {
        next.add(topicId);
      } else {
        next.delete(topicId);
      }
      return next;
    });
  }
}
