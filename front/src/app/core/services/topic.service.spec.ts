import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { Topic } from '../models/topic';
import { TopicService } from './topic.service';

describe('TopicService', () => {
  it('should fetch every topic from the API', () => {
    // Arrange
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const http = TestBed.inject(HttpTestingController);
    const topics: Topic[] = [{ id: 1, name: 'Java', description: 'The JVM ecosystem', subscribed: false }];
    let received: Topic[] | undefined;

    // Act
    TestBed.inject(TopicService)
      .getTopics()
      .subscribe((result) => (received = result));
    http.expectOne('/api/topics').flush(topics);

    // Assert
    expect(received).toEqual(topics);
    http.verify();
  });

  it('should send the subscription to the topic endpoint', () => {
    // Arrange
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const http = TestBed.inject(HttpTestingController);

    // Act
    TestBed.inject(TopicService).subscribe(2).subscribe();
    const request = http.expectOne('/api/topics/2/subscribe');
    request.flush(null, { status: 204, statusText: 'No Content' });

    // Assert
    expect(request.request.method).toBe('POST');
    http.verify();
  });

  it('should send the unsubscription to the topic endpoint', () => {
    // Arrange
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const http = TestBed.inject(HttpTestingController);

    // Act
    TestBed.inject(TopicService).unsubscribe(2).subscribe();
    const request = http.expectOne('/api/topics/2/subscribe');
    request.flush(null, { status: 204, statusText: 'No Content' });

    // Assert
    expect(request.request.method).toBe('DELETE');
    http.verify();
  });
});
