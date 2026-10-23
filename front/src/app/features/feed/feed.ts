import { DatePipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { map, startWith, switchMap } from 'rxjs';

import { FeedSort, Post } from '../../core/models/post';
import { PostService } from '../../core/services/post.service';
import { Header } from '../../shared/components/header/header';

/**
 * News feed of the logged-in user: the articles of the topics they are subscribed to, sortable by date.
 */
@Component({
  selector: 'app-feed',
  imports: [Header, DatePipe, RouterLink],
  templateUrl: './feed.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Feed {
  private readonly postService = inject(PostService);

  /** Most recent first by default, as required by the specifications. */
  protected readonly sort = signal<FeedSort>('desc');

  /**
   * Undefined while loading. Changing the sort order reloads the feed; switchMap drops the answer of a previous
   * request still in progress, so that a fast double click never displays the wrong order.
   */
  protected readonly posts = toSignal(
    toObservable(this.sort).pipe(
      switchMap((sort) =>
        this.postService.getFeed(sort).pipe(
          map((posts): Post[] | undefined => posts),
          startWith(undefined),
        ),
      ),
    ),
  );

  protected toggleSort(): void {
    this.sort.update((sort) => (sort === 'desc' ? 'asc' : 'desc'));
  }
}
