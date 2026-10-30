import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

import { Home } from './home';

describe('Home', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Home],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('should lead visitors to the login and registration forms', () => {
    // Arrange
    const fixture = TestBed.createComponent(Home);

    // Act
    fixture.detectChanges();

    // Assert
    const links = Array.from((fixture.nativeElement as HTMLElement).querySelectorAll('a'));
    expect(links.map((link) => link.getAttribute('href'))).toEqual(['/login', '/register']);
  });
});
