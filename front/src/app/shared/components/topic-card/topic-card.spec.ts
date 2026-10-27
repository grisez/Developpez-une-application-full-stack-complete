import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';

import { Topic } from '../../../core/models/topic';
import { TopicCard } from './topic-card';

@Component({
  imports: [TopicCard],
  template: `
    <app-topic-card [topic]="topic" [headingLevel]="3">
      <button type="button">Action</button>
    </app-topic-card>
  `,
})
class HostComponent {
  readonly topic: Topic = { id: 1, name: 'Java', description: 'The JVM ecosystem', subscribed: false };
}

describe('TopicCard', () => {
  it('should show the topic with the requested heading level and the projected action', () => {
    // Act
    const fixture = TestBed.createComponent(HostComponent);
    fixture.detectChanges();

    // Assert
    const card = (fixture.nativeElement as HTMLElement).querySelector('app-topic-card')!;
    expect(card.querySelector('h3')?.textContent).toContain('Java');
    expect(card.querySelector('h2')).toBeNull();
    expect(card.textContent).toContain('The JVM ecosystem');
    expect(card.querySelector('button')?.textContent).toContain('Action');
  });
});
