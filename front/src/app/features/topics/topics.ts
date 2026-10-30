import { ChangeDetectionStrategy, Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { Topic } from '../../core/models/topic';
import { TopicService } from '../../core/services/topic.service';
import { Header } from '../../shared/components/header/header';
import { LoadError } from '../../shared/components/load-error/load-error';
import { TopicCard } from '../../shared/components/topic-card/topic-card';
import { LoadState, toLoadState } from '../../shared/utils/load-state';

/**
 * Page listing every topic, whether the user is subscribed to it or not, with a button to subscribe.
 */
@Component({
  selector: 'app-topics',
  imports: [Header, TopicCard, LoadError],
  templateUrl: './topics.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Topics {
  private readonly topicService = inject(TopicService);
  private readonly destroyRef = inject(DestroyRef);

  protected readonly topics = signal<LoadState<Topic[]>>({ status: 'loading' });

  /** Topics whose subscription request is in progress, to prevent a double click. */
  protected readonly pending = signal<ReadonlySet<number>>(new Set());

  constructor() {
    this.load();
  }

  /** Loads the topics; also called by the retry button after an error. */
  protected load(): void {
    this.topicService
      .getTopics()
      .pipe(toLoadState(), takeUntilDestroyed(this.destroyRef))
      .subscribe((state) => this.topics.set(state));
  }

  protected subscribe(topic: Topic): void {
    this.setPending(topic.id, true);
    this.topicService.subscribe(topic.id).subscribe({
      next: () => {
        this.topics.update((state) =>
          state.status === 'loaded'
            ? {
                ...state,
                value: state.value.map((current) =>
                  current.id === topic.id ? { ...current, subscribed: true } : current,
                ),
              }
            : state,
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
