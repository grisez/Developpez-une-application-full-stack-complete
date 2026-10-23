import { HttpErrorResponse } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { NonNullableFormBuilder, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Profile as ProfileModel } from '../../core/models/profile';
import { AuthService } from '../../core/services/auth.service';
import { ProfileService } from '../../core/services/profile.service';
import { Header } from '../../shared/components/header/header';
import { TopicCard } from '../../shared/components/topic-card/topic-card';
import { TrimDirective } from '../../shared/directives/trim.directive';
import {
  accountErrorMessage,
  emailValidators,
  usernameValidators,
} from '../../shared/validators/account.validators';
import { passwordValidator } from '../../shared/validators/password.validator';

/**
 * Profile page of the logged-in user: a form to update their information, then their subscriptions.
 */
@Component({
  selector: 'app-profile',
  imports: [Header, TopicCard, RouterLink, ReactiveFormsModule, TrimDirective],
  templateUrl: './profile.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Profile {
  private readonly profileService = inject(ProfileService);
  private readonly authService = inject(AuthService);

  /** Undefined while loading; an error is reported by the error interceptor. */
  protected readonly profile = signal<ProfileModel | undefined>(undefined);

  /** The password is left empty: it is only stored as a one-way hash, and an empty value keeps it unchanged. */
  protected readonly form = inject(NonNullableFormBuilder).group({
    username: ['', usernameValidators],
    email: ['', emailValidators],
    password: ['', passwordValidator],
  });

  protected readonly saving = signal(false);

  protected readonly message = signal<{ text: string; error: boolean } | null>(null);

  constructor() {
    this.profileService
      .getProfile()
      .pipe(takeUntilDestroyed())
      .subscribe((profile) => this.showProfile(profile));
  }

  protected save(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    this.saving.set(true);
    this.message.set(null);
    this.profileService.updateProfile(this.form.getRawValue()).subscribe({
      next: (profile) => {
        this.showProfile(profile);
        this.authService.updateCurrentUser({ id: profile.id, username: profile.username, email: profile.email });
        this.saving.set(false);
        this.message.set({ text: 'Votre profil a été mis à jour.', error: false });
      },
      error: (error: HttpErrorResponse) => {
        this.saving.set(false);
        this.message.set({ text: accountErrorMessage(error), error: true });
      },
    });
  }

  /** Whether the field should display its error: invalid and already visited by the user. */
  protected showError(field: 'username' | 'email' | 'password'): boolean {
    const control = this.form.controls[field];
    return control.invalid && control.touched;
  }

  private showProfile(profile: ProfileModel): void {
    this.profile.set(profile);
    this.form.reset({ username: profile.username, email: profile.email, password: '' });
  }
}
