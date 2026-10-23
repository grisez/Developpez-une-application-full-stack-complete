import { HttpErrorResponse } from '@angular/common/http';
import { signal } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';
import { PostService } from '../../../core/services/post.service';
import { TopicService } from '../../../core/services/topic.service';
import { CreatePost } from './create-post';

describe('CreatePost', () => {
  let postService: jasmine.SpyObj<PostService>;

  beforeEach(() => {
    postService = jasmine.createSpyObj<PostService>('PostService', ['create']);
    TestBed.configureTestingModule({
      imports: [CreatePost],
      providers: [
        provideRouter([]),
        { provide: PostService, useValue: postService },
        {
          provide: TopicService,
          useValue: { getTopics: () => of([{ id: 2, name: 'Java', description: 'JVM', subscribed: false }]) },
        },
        { provide: AuthService, useValue: { isLoggedIn: signal(true) } },
      ],
    });
  });

  function render(): { fixture: ComponentFixture<CreatePost>; element: HTMLElement } {
    const fixture = TestBed.createComponent(CreatePost);
    fixture.detectChanges();
    return { fixture, element: fixture.nativeElement as HTMLElement };
  }

  function fill(fixture: ComponentFixture<CreatePost>, element: HTMLElement): void {
    const select = element.querySelector<HTMLSelectElement>('#topic')!;
    select.selectedIndex = 1;
    select.dispatchEvent(new Event('change'));
    for (const [id, value] of [['title', 'Hello'], ['content', 'First post']]) {
      const field = element.querySelector<HTMLInputElement | HTMLTextAreaElement>(`#${id}`)!;
      field.value = value;
      field.dispatchEvent(new Event('input'));
    }
    fixture.detectChanges();
  }

  function submit(fixture: ComponentFixture<CreatePost>, element: HTMLElement): void {
    element.querySelector('form')!.dispatchEvent(new Event('submit'));
    fixture.detectChanges();
  }

  it('should require a topic, a title and a content', () => {
    // Arrange
    const { fixture, element } = render();

    // Act
    submit(fixture, element);

    // Assert
    expect(postService.create).not.toHaveBeenCalled();
    expect(element.querySelector('#topic-error')).not.toBeNull();
    expect(element.querySelector('#title-error')).not.toBeNull();
    expect(element.querySelector('#content-error')).not.toBeNull();
  });

  it('should publish the article and go back to the feed', () => {
    // Arrange
    postService.create.and.returnValue(of({
      id: 5, title: 'Hello', content: 'First post', createdAt: '2026-10-23T10:00:00Z',
      authorUsername: 'alice', topicId: 2, topicName: 'Java',
    }));
    const navigate = spyOn(TestBed.inject(Router), 'navigateByUrl');
    const { fixture, element } = render();
    fill(fixture, element);

    // Act
    submit(fixture, element);

    // Assert
    expect(postService.create).toHaveBeenCalledWith({ topicId: 2, title: 'Hello', content: 'First post' });
    expect(navigate).toHaveBeenCalledWith('/feed');
  });

  it('should explain the failure and keep the form when publishing fails', () => {
    // Arrange
    postService.create.and.returnValue(throwError(() => new HttpErrorResponse({ status: 400 })));
    const { fixture, element } = render();
    fill(fixture, element);

    // Act
    submit(fixture, element);

    // Assert
    expect(element.querySelector('[role="alert"]')).not.toBeNull();
    expect(element.querySelector<HTMLInputElement>('#title')!.value).toBe('Hello');
  });
});
