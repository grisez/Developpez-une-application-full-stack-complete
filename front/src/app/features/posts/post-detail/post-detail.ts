import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, input, numberAttribute, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Observable, catchError, map, of, startWith, switchMap, tap, throwError } from 'rxjs';

import { PostComment, PostDetail as PostDetailModel } from '../../../core/models/post';
import { PostService } from '../../../core/services/post.service';
import { Header } from '../../../shared/components/header/header';

/** What the page displays: the article, a "not found" message, or nothing yet while loading. */
type PageState = { status: 'loading' } | { status: 'loaded'; post: PostDetailModel } | { status: 'not-found' };

/**
 * Page of an article: its topic, title, author, date, content and comments, with a form to comment.
 */
@Component({
  selector: 'app-post-detail',
  imports: [Header, DatePipe, RouterLink, ReactiveFormsModule],
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

  /** Comments posted from this page, displayed after the loaded ones without reloading the article. */
  private readonly addedComments = signal<PostComment[]>([]);

  protected readonly comments = computed(() => {
    const state = this.state();
    return state.status === 'loaded' ? [...state.post.comments, ...this.addedComments()] : [];
  });

  /** Same limit as the back-end; the pattern refuses a comment made of spaces only. */
  protected readonly commentForm = inject(NonNullableFormBuilder).group({
    content: ['', [Validators.required, Validators.maxLength(1000), Validators.pattern(/\S/)]],
  });

  protected readonly sending = signal(false);

  protected readonly sendError = signal(false);

  protected sendComment(): void {
    const content = this.commentForm.controls.content;
    if (this.commentForm.invalid) {
      content.markAsTouched();
      return;
    }
    this.sending.set(true);
    this.sendError.set(false);
    this.postService.addComment(this.id(), content.value).subscribe({
      next: (comment) => {
        this.addedComments.update((comments) => [...comments, comment]);
        this.commentForm.reset();
        this.sending.set(false);
      },
      error: () => {
        this.sending.set(false);
        this.sendError.set(true);
      },
    });
  }

  /** Whether the comment field should display its error: invalid and already visited by the user. */
  protected showCommentError(): boolean {
    const content = this.commentForm.controls.content;
    return content.invalid && content.touched;
  }

  private load(id: number): Observable<PageState> {
    return this.postService.getById(id).pipe(
      tap(() => this.addedComments.set([])),
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
