import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, input, numberAttribute } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';
import { Observable, catchError, map, of, startWith, switchMap, throwError } from 'rxjs';

import { PostDetail as PostDetailModel } from '../../../core/models/post';
import { PostService } from '../../../core/services/post.service';
import { Header } from '../../../shared/components/header/header';

/** What the page displays: the article, a "not found" message, or nothing yet while loading. */
type PageState = { status: 'loading' } | { status: 'loaded'; post: PostDetailModel } | { status: 'not-found' };

/**
 * Page of an article: its topic, title, author, date, content and comments.
 */
@Component({
  selector: 'app-post-detail',
  imports: [Header, DatePipe, RouterLink],
  templateUrl: './post-detail.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostDetail {
  private readonly postService = inject(PostService);

  /** Article id taken from the URL, bound by the router (withComponentInputBinding). */
  readonly id = input.required({ transform: numberAttribute });

  protected readonly state = toSignal(
    toObservable(this.id).pipe(switchMap((id) => this.load(id))),
    { initialValue: { status: 'loading' } as PageState },
  );

  private load(id: number): Observable<PageState> {
    return this.postService.getById(id).pipe(
      map((post): PageState => ({ status: 'loaded', post })),
      catchError((error: unknown) =>
        // A missing article is expected and shown on the page; other errors are reported by the interceptor
        error instanceof HttpErrorResponse && (error.status === 404 || error.status === 400)
          ? of<PageState>({ status: 'not-found' })
          : throwError(() => error),
      ),
      startWith<PageState>({ status: 'loading' }),
    );
  }
}
