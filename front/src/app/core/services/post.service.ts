import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { CreatePostRequest, FeedSort, Post } from '../models/post';

/**
 * Talks to the article endpoints.
 */
@Injectable({ providedIn: 'root' })
export class PostService {
  private readonly http = inject(HttpClient);

  /** The articles of the topics the logged-in user is subscribed to. */
  getFeed(sort: FeedSort): Observable<Post[]> {
    return this.http.get<Post[]>('/api/articles', { params: { sort } });
  }

  /** Publishes an article written by the logged-in user. */
  create(request: CreatePostRequest): Observable<Post> {
    return this.http.post<Post>('/api/articles', request);
  }
}
