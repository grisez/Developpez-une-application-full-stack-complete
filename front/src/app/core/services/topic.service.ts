import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { Topic } from '../models/topic';

/**
 * Talks to the topic endpoints.
 */
@Injectable({ providedIn: 'root' })
export class TopicService {
  private readonly http = inject(HttpClient);

  /** Every topic, with the subscription state of the logged-in user. */
  getTopics(): Observable<Topic[]> {
    return this.http.get<Topic[]>('/api/topics');
  }

  /** Subscribes the logged-in user to a topic; subscribing twice has no effect. */
  subscribe(topicId: number): Observable<void> {
    return this.http.post<void>(`/api/topics/${topicId}/subscribe`, null);
  }

  /** Unsubscribes the logged-in user from a topic; unsubscribing twice has no effect. */
  unsubscribe(topicId: number): Observable<void> {
    return this.http.delete<void>(`/api/topics/${topicId}/subscribe`);
  }
}
