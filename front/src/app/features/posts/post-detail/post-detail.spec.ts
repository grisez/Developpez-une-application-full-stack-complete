import { registerLocaleData } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import localeFr from '@angular/common/locales/fr';
import { LOCALE_ID, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Title } from '@angular/platform-browser';
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

  let addComment: jasmine.Spy;

  function render(response: Observable<PostDetailModel>): HTMLElement {
    addComment = jasmine.createSpy('addComment');
    TestBed.configureTestingModule({
      imports: [PostDetail],
      providers: [
        provideRouter([]),
        { provide: PostService, useValue: { getById: () => response, addComment } },
        { provide: AuthService, useValue: { isLoggedIn: signal(true) } },
        { provide: LOCALE_ID, useValue: 'fr-FR' },
      ],
    });
    fixture = TestBed.createComponent(PostDetail);
    fixture.componentRef.setInput('id', '5');
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  let fixture: ComponentFixture<PostDetail>;

  function typeAndSend(element: HTMLElement, content: string): void {
    const textarea = element.querySelector<HTMLTextAreaElement>('#comment')!;
    textarea.value = content;
    textarea.dispatchEvent(new Event('input'));
    element.querySelector('section form')!.dispatchEvent(new Event('submit'));
    fixture.detectChanges();
  }

  it('should name the browser tab after the article', () => {
    // Arrange and Act
    render(of(article));

    // Assert
    expect(TestBed.inject(Title).getTitle()).toBe('Hello MDD - MDD');
  });

  it('should add the new comment to the list and clear the field', () => {
    // Arrange
    const element = render(of(article));
    addComment.and.returnValue(
      of({ id: 10, content: 'Thanks!', createdAt: '2026-10-24T10:00:00Z', authorUsername: 'carol' }),
    );

    // Act
    typeAndSend(element, 'Thanks!');

    // Assert
    expect(addComment).toHaveBeenCalledWith(5, 'Thanks!');
    const comments = element.querySelectorAll('section li');
    expect(comments.length).toBe(2);
    expect(comments[1].textContent).toContain('carol');
    expect(element.querySelector<HTMLTextAreaElement>('#comment')!.value).toBe('');
  });

  it('should not send an empty comment', () => {
    // Arrange
    const element = render(of(article));

    // Act
    typeAndSend(element, '   ');

    // Assert
    expect(addComment).not.toHaveBeenCalled();
    expect(element.querySelector('#comment-error')).not.toBeNull();
  });

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
