import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { CreatePostRequest, FeedSort, Post, PostComment, PostDetail } from '../models/post';

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

  /** An article with its comments, oldest first. */
  getById(id: number): Observable<PostDetail> {
    return this.http.get<PostDetail>(`/api/articles/${id}`);
  }

  /** Adds a comment written by the logged-in user to an article. */
  addComment(postId: number, content: string): Observable<PostComment> {
    return this.http.post<PostComment>(`/api/articles/${postId}/comments`, { content });
  }

  /** Publishes an article written by the logged-in user. */
  create(request: CreatePostRequest): Observable<Post> {
    return this.http.post<Post>('/api/articles', request);
  }
}
