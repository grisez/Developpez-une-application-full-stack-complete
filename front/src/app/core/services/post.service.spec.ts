import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { PostService } from './post.service';

describe('PostService', () => {
  it('should request the feed in the given order', () => {
    // Arrange
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const http = TestBed.inject(HttpTestingController);

    // Act
    TestBed.inject(PostService).getFeed('asc').subscribe();
    const request = http.expectOne((req) => req.url === '/api/articles');
    request.flush([]);

    // Assert
    expect(request.request.params.get('sort')).toBe('asc');
    http.verify();
  });

  it('should fetch an article by its id', () => {
    // Arrange
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const http = TestBed.inject(HttpTestingController);

    // Act
    TestBed.inject(PostService).getById(5).subscribe();
    const request = http.expectOne('/api/articles/5');
    request.flush({});

    // Assert
    expect(request.request.method).toBe('GET');
    http.verify();
  });

  it('should send a comment to the comments of the article', () => {
    // Arrange
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const http = TestBed.inject(HttpTestingController);

    // Act
    TestBed.inject(PostService).addComment(5, 'Nice post').subscribe();
    const request = http.expectOne('/api/articles/5/comments');
    request.flush({});

    // Assert
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ content: 'Nice post' });
    http.verify();
  });

  it('should send a new article with POST', () => {
    // Arrange
    TestBed.configureTestingModule({ providers: [provideHttpClient(), provideHttpClientTesting()] });
    const http = TestBed.inject(HttpTestingController);
    const article = { topicId: 2, title: 'Hello', content: 'First post' };

    // Act
    TestBed.inject(PostService).create(article).subscribe();
    const request = http.expectOne('/api/articles');
    request.flush({});

    // Assert
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(article);
    http.verify();
  });
});
