import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, computed, inject, input, numberAttribute, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Title } from '@angular/platform-browser';
import { RouterLink } from '@angular/router';
import { Observable, catchError, map, of, startWith, switchMap, tap } from 'rxjs';

import { PostComment, PostDetail as PostDetailModel } from '../../../core/models/post';
import { PostService } from '../../../core/services/post.service';
import { Header } from '../../../shared/components/header/header';
import { LoadError } from '../../../shared/components/load-error/load-error';

/** What the page displays: the article, a "not found" message, an error with a retry, or nothing yet while loading. */
type PageState =
  { status: 'loading' } | { status: 'loaded'; post: PostDetailModel } | { status: 'not-found' } | { status: 'error' };

/**
 * Page of an article: its topic, title, author, date, content and comments, with a form to comment.
 */
@Component({
  selector: 'app-post-detail',
  imports: [Header, DatePipe, RouterLink, ReactiveFormsModule, LoadError],
  templateUrl: './post-detail.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PostDetail {
  private readonly postService = inject(PostService);

  private readonly title = inject(Title);

  /** Article id taken from the URL, bound by the router (withComponentInputBinding). */
  readonly id = input.required({ transform: numberAttribute });

  /** Incremented by the retry button, to reload the article after an error. */
  private readonly attempt = signal(0);

  protected readonly state = toSignal(
    toObservable(computed(() => ({ id: this.id(), attempt: this.attempt() }))).pipe(
      switchMap(({ id }) => this.load(id)),
    ),
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

  protected retry(): void {
    this.attempt.update((attempt) => attempt + 1);
  }

  private load(id: number): Observable<PageState> {
    return this.postService.getById(id).pipe(
      tap((post) => {
        this.addedComments.set([]);
        // The route title only says "Article": each article page gets its own title (RGAA 8.6)
        this.title.setTitle(`${post.title} - MDD`);
      }),
      map((post): PageState => ({ status: 'loaded', post })),
      catchError((error: unknown) =>
        // A missing article is a normal case with its own message; any other error can be retried
        of<PageState>(
          error instanceof HttpErrorResponse && (error.status === 404 || error.status === 400)
            ? { status: 'not-found' }
            : { status: 'error' },
        ),
      ),
      startWith<PageState>({ status: 'loading' }),
    );
  }
}
