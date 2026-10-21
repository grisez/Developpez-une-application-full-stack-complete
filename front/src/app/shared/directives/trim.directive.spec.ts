import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { FormControl, ReactiveFormsModule } from '@angular/forms';

import { TrimDirective } from './trim.directive';

@Component({
  imports: [ReactiveFormsModule, TrimDirective],
  template: '<input appTrim [formControl]="control" />',
})
class HostComponent {
  readonly control = new FormControl('');
}

describe('TrimDirective', () => {
  function render() {
    const fixture = TestBed.createComponent(HostComponent);
    fixture.detectChanges();
    const input = (fixture.nativeElement as HTMLElement).querySelector('input')!;
    const type = (value: string) => {
      input.value = value;
      input.dispatchEvent(new Event('input'));
    };
    return { control: fixture.componentInstance.control, input, type };
  }

  it('should remove the surrounding spaces when the field loses focus', () => {
    // Arrange
    const { control, input, type } = render();
    type('  alice@mdd.dev ');

    // Act
    input.dispatchEvent(new Event('blur'));

    // Assert
    expect(control.value).toBe('alice@mdd.dev');
    expect(input.value).toBe('alice@mdd.dev');
  });

  it('should remove the surrounding spaces when Enter submits the form', () => {
    // Arrange
    const { control, input, type } = render();
    type('alice ');

    // Act
    input.dispatchEvent(new KeyboardEvent('keydown', { key: 'Enter' }));

    // Assert
    expect(control.value).toBe('alice');
  });

  it('should keep the spaces inside the value', () => {
    // Arrange
    const { control, input, type } = render();
    type(' a b ');

    // Act
    input.dispatchEvent(new Event('blur'));

    // Assert
    expect(control.value).toBe('a b');
  });
});
