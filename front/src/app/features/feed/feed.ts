import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { switchMap } from 'rxjs';

import { FeedSort, Post } from '../../core/models/post';
import { PostService } from '../../core/services/post.service';
import { Header } from '../../shared/components/header/header';
import { LoadError } from '../../shared/components/load-error/load-error';
import { LoadState, toLoadState } from '../../shared/utils/load-state';

/**
 * News feed of the logged-in user: the articles of the topics they are subscribed to, sortable by date.
 */
@Component({
  selector: 'app-feed',
  imports: [Header, DatePipe, RouterLink, LoadError],
  templateUrl: './feed.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Feed {
  private readonly postService = inject(PostService);

  /** Most recent first by default, as required by the specifications. */
  protected readonly sort = signal<FeedSort>('desc');

  /** Incremented by the retry button, to reload the feed after an error. */
  private readonly attempt = signal(0);

  /**
   * Changing the sort order reloads the feed; switchMap drops the answer of a previous request still in progress,
   * so that a fast double click never displays the wrong order.
   */
  protected readonly posts = toSignal(
    toObservable(computed(() => ({ sort: this.sort(), attempt: this.attempt() }))).pipe(
      switchMap(({ sort }) => this.postService.getFeed(sort).pipe(toLoadState())),
    ),
    { initialValue: { status: 'loading' } as LoadState<Post[]> },
  );

  protected toggleSort(): void {
    this.sort.update((sort) => (sort === 'desc' ? 'asc' : 'desc'));
  }

  protected retry(): void {
    this.attempt.update((attempt) => attempt + 1);
  }
}
