import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { PostService } from '../../../core/services/post.service';
import { TopicService } from '../../../core/services/topic.service';
import { Header } from '../../../shared/components/header/header';
import { TrimDirective } from '../../../shared/directives/trim.directive';

/**
 * Form to publish an article on a topic. The author and the date are set by the server.
 */
@Component({
  selector: 'app-create-post',
  imports: [Header, ReactiveFormsModule, RouterLink, TrimDirective],
  templateUrl: './create-post.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CreatePost {
  private readonly postService = inject(PostService);
  private readonly router = inject(Router);

  /** Every topic can receive an article, whether the author follows it or not. */
  protected readonly topics = toSignal(inject(TopicService).getTopics(), { initialValue: [] });

  /** Same limits as the back-end: a 150-character title and a 10,000-character content. */
  protected readonly form = inject(NonNullableFormBuilder).group({
    topicId: [0, Validators.min(1)],
    title: ['', [Validators.required, Validators.maxLength(150)]],
    content: ['', [Validators.required, Validators.maxLength(10_000)]],
  });

  protected readonly submitting = signal(false);

  protected readonly serverError = signal<string | null>(null);

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.submitting.set(true);
    this.serverError.set(null);
    this.postService.create(this.form.getRawValue()).subscribe({
      next: () => this.router.navigateByUrl('/feed'),
      error: () => {
        this.submitting.set(false);
        this.serverError.set("L'article n'a pas pu être publié. Vérifiez votre saisie puis réessayez.");
      },
    });
  }

  /** Whether the field should display its error: invalid and already visited by the user. */
  protected showError(field: 'topicId' | 'title' | 'content'): boolean {
    const control = this.form.controls[field];
    return control.invalid && control.touched;
  }
}
