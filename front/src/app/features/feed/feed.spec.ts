import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import { LOCALE_ID, signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { Post } from '../../core/models/post';
import { AuthService } from '../../core/services/auth.service';
import { PostService } from '../../core/services/post.service';
import { Feed } from './feed';

describe('Feed', () => {
  const post: Post = {
    id: 1,
    title: 'Hello MDD',
    content: 'First post',
    createdAt: '2026-10-20T10:00:00Z',
    authorUsername: 'alice',
    topicId: 2,
    topicName: 'Java',
  };
  let postService: jasmine.SpyObj<PostService>;

  beforeAll(() => registerLocaleData(localeFr));

  beforeEach(() => {
    postService = jasmine.createSpyObj<PostService>('PostService', ['getFeed']);
    TestBed.configureTestingModule({
      imports: [Feed],
      providers: [
        provideRouter([]),
        { provide: PostService, useValue: postService },
        { provide: AuthService, useValue: { isLoggedIn: signal(true) } },
        { provide: LOCALE_ID, useValue: 'fr-FR' },
      ],
    });
  });

  function render(): { fixture: ComponentFixture<Feed>; element: HTMLElement } {
    const fixture = TestBed.createComponent(Feed);
    fixture.detectChanges();
    return { fixture, element: fixture.nativeElement as HTMLElement };
  }

  it('should list the articles, most recent first, with their date and author', () => {
    // Arrange
    postService.getFeed.and.returnValue(of([post]));

    // Act
    const { element } = render();

    // Assert
    expect(postService.getFeed).toHaveBeenCalledWith('desc');
    const card = element.querySelector('main li')!;
    expect(card.querySelector('h2')?.textContent).toContain('Hello MDD');
    expect(card.textContent).toContain('20 octobre 2026');
    expect(card.textContent).toContain('alice');
  });

  it('should reload the feed oldest first when the sort order is toggled', () => {
    // Arrange
    postService.getFeed.and.returnValue(of([post]));
    const { fixture, element } = render();

    // Act
    element.querySelector<HTMLButtonElement>('main button')!.click();
    fixture.detectChanges();

    // Assert
    expect(postService.getFeed).toHaveBeenCalledWith('asc');
    expect(element.querySelector('main button')?.textContent).toContain('↑');
  });

  it('should invite the user to subscribe to topics when the feed is empty', () => {
    // Arrange
    postService.getFeed.and.returnValue(of([]));

    // Act
    const { element } = render();

    // Assert
    expect(element.querySelector('main a[href="/topics"]')).not.toBeNull();
  });
});
