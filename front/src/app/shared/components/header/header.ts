import { NgTemplateOutlet } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';

/**
 * Top bar with the MDD logo and, for a logged-in user, the navigation: inline on desktop,
 * in a side menu opened by a burger button on mobile.
 */
@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, NgTemplateOutlet],
  templateUrl: './header.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    '(document:keydown.escape)': 'closeMenu()',
  },
})
export class Header {
  private readonly router = inject(Router);

  protected readonly authService = inject(AuthService);

  protected readonly menuOpen = signal(false);

  protected toggleMenu(): void {
    this.menuOpen.update((open) => !open);
  }

  protected closeMenu(): void {
    this.menuOpen.set(false);
  }

  protected logout(): void {
    this.closeMenu();
    this.authService.logout().subscribe(() => this.router.navigateByUrl('/'));
  }
}
