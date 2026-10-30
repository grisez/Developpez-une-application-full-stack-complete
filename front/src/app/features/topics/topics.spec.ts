import { HttpErrorResponse } from '@angular/common/http';
import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Subject, of, throwError } from 'rxjs';

import { Topic } from '../../core/models/topic';
import { AuthService } from '../../core/services/auth.service';
import { TopicService } from '../../core/services/topic.service';
import { Topics } from './topics';

describe('Topics', () => {
  const topics: Topic[] = [
    { id: 1, name: 'Java', description: 'The JVM ecosystem', subscribed: true },
    { id: 2, name: 'Angular', description: 'Front-end framework', subscribed: false },
  ];
  let topicService: jasmine.SpyObj<TopicService>;

  beforeEach(async () => {
    topicService = jasmine.createSpyObj<TopicService>('TopicService', ['getTopics', 'subscribe']);
    await TestBed.configureTestingModule({
      imports: [Topics],
      providers: [
        provideRouter([]),
        { provide: TopicService, useValue: topicService },
        { provide: AuthService, useValue: { isLoggedIn: signal(true) } },
      ],
    }).compileComponents();
  });

  function render(): HTMLElement {
    const fixture = TestBed.createComponent(Topics);
    fixture.detectChanges();
    return fixture.nativeElement as HTMLElement;
  }

  it('should list every topic with its name and description', () => {
    // Arrange
    topicService.getTopics.and.returnValue(of(topics));

    // Act
    const cards = render().querySelectorAll('li');

    // Assert
    expect(cards.length).toBe(2);
    expect(cards[0].querySelector('h2')?.textContent).toContain('Java');
    expect(cards[1].textContent).toContain('Front-end framework');
  });

  it('should show a disabled button for the topics the user is already subscribed to', () => {
    // Arrange
    topicService.getTopics.and.returnValue(of(topics));

    // Act
    const buttons = render().querySelectorAll<HTMLButtonElement>('li button');

    // Assert
    expect(buttons[0].textContent).toContain('Déjà abonné');
    expect(buttons[0].disabled).toBeTrue();
    expect(buttons[1].textContent).toContain("S'abonner");
    expect(buttons[1].disabled).toBeFalse();
  });

  it('should tell the user that the topics are loading', () => {
    // Arrange
    topicService.getTopics.and.returnValue(new Subject<Topic[]>());

    // Act and assert
    expect(render().querySelector('[role="status"]')?.textContent).toContain('Chargement');
  });

  it('should offer to retry when the topics cannot be loaded, then display them', () => {
    // Arrange
    topicService.getTopics.and.returnValues(
      throwError(() => new HttpErrorResponse({ status: 500 })),
      of(topics),
    );
    const fixture = TestBed.createComponent(Topics);
    fixture.detectChanges();
    const element = fixture.nativeElement as HTMLElement;
    expect(element.querySelector('[role="alert"]')?.textContent).toContain("Les thèmes n'ont pas pu être chargés.");

    // Act
    element.querySelector<HTMLButtonElement>('[role="alert"] button')!.click();
    fixture.detectChanges();

    // Assert
    expect(element.querySelector('[role="alert"]')).toBeNull();
    expect(element.querySelectorAll('li').length).toBe(2);
  });

  it('should subscribe to a topic and then show it as already subscribed', () => {
    // Arrange
    topicService.getTopics.and.returnValue(of(topics));
    topicService.subscribe.and.returnValue(of(undefined));
    const fixture = TestBed.createComponent(Topics);
    fixture.detectChanges();
    const element = fixture.nativeElement as HTMLElement;

    // Act
    element.querySelectorAll<HTMLButtonElement>('li button')[1].click();
    fixture.detectChanges();

    // Assert
    const button = element.querySelectorAll<HTMLButtonElement>('li button')[1];
    expect(topicService.subscribe).toHaveBeenCalledWith(2);
    expect(button.textContent).toContain('Déjà abonné');
    expect(button.disabled).toBeTrue();
  });

  it('should keep the subscribe button available when the request fails', () => {
    // Arrange
    topicService.getTopics.and.returnValue(of(topics));
    topicService.subscribe.and.returnValue(throwError(() => new Error('Server error')));
    const fixture = TestBed.createComponent(Topics);
    fixture.detectChanges();
    const element = fixture.nativeElement as HTMLElement;

    // Act
    element.querySelectorAll<HTMLButtonElement>('li button')[1].click();
    fixture.detectChanges();

    // Assert
    const button = element.querySelectorAll<HTMLButtonElement>('li button')[1];
    expect(button.textContent).toContain("S'abonner");
    expect(button.disabled).toBeFalse();
  });
});
