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
});
