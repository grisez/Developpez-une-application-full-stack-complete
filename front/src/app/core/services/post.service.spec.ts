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
});
