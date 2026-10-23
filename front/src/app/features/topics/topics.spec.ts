import { signal } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { Subject, of } from 'rxjs';

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
    topicService = jasmine.createSpyObj<TopicService>('TopicService', ['getTopics']);
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
});
