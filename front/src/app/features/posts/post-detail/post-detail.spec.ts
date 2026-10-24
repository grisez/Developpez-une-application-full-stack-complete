import { registerLocaleData } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import localeFr from '@angular/common/locales/fr';
import { LOCALE_ID, signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Observable, of, throwError } from 'rxjs';

import { PostDetail as PostDetailModel } from '../../../core/models/post';
import { AuthService } from '../../../core/services/auth.service';
import { PostService } from '../../../core/services/post.service';
import { PostDetail } from './post-detail';

describe('PostDetail', () => {
  const article: PostDetailModel = {
    id: 5,
    title: 'Hello MDD',
    content: 'First post',
    createdAt: '2026-10-20T10:00:00Z',
    authorUsername: 'alice',
    topicId: 2,
    topicName: 'Java',
    comments: [{ id: 9, content: 'Nice post', createdAt: '2026-10-21T08:00:00Z', authorUsername: 'bob' }],
  };

  beforeAll(() => registerLocaleData(localeFr));

  function render(response: Observable<PostDetailModel>): HTMLElement {
    TestBed.configureTestingModule({
      imports: [PostDetail],
      providers: [
        provideRouter([]),
        { provide: PostService, useValue: { getById: () => response } },
        { provide: AuthService, useValue: { isLoggedIn: signal(true) } },
        { provide: LOCALE_ID, useValue: 'fr-FR' },
      ],
    });
    const fixture = TestBed.createComponent(PostDetail);
    fixture.componentRef.setInput('id', '5');
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  it('should display the topic, title, author, date, content and comments', () => {
    // Act
    const element = render(of(article));

    // Assert
    const text = element.querySelector('article')?.textContent ?? '';
    expect(element.querySelector('h1')?.textContent).toContain('Hello MDD');
    expect(text).toContain('20 octobre 2026');
    expect(text).toContain('alice');
    expect(text).toContain('Java');
    expect(text).toContain('First post');
    expect(element.querySelector('section li')?.textContent).toContain('Nice post');
  });

  it('should explain that the article does not exist when the API answers 404', () => {
    // Act
    const element = render(throwError(() => new HttpErrorResponse({ status: 404 })));

    // Assert
    expect(element.querySelector('h1')?.textContent).toContain('Article introuvable');
    expect(element.querySelector('main a[href="/feed"]')).not.toBeNull();
  });
});
