import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { App } from './app';

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('should render the router outlet', () => {
    // Arrange
    const fixture = TestBed.createComponent(App);

    // Act
    fixture.detectChanges();

    // Assert
    const element = fixture.nativeElement as HTMLElement;
    expect(element.querySelector('router-outlet')).not.toBeNull();
  });

  it('should move the focus to the main content when the skip link is used', () => {
    // Arrange
    const main = document.createElement('main');
    document.body.appendChild(main);
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    const skipLink = (fixture.nativeElement as HTMLElement).querySelector<HTMLAnchorElement>('a[href="#main"]')!;

    // Act
    skipLink.click();

    // Assert
    expect(document.activeElement).toBe(main);
    main.remove();
  });
});
