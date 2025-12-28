import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { AuthLayout } from '../../../layout/auth-layout/auth-layout';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth/auth-service';

@Component({
  selector: 'app-reset-password-request',
  imports: [AuthLayout, RouterLink],
  templateUrl: './reset-password-request.html',
  styleUrl: './reset-password-request.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResetPasswordRequest {
  private auth = inject(AuthService);

  readonly email = signal('');
  readonly loading = signal(false);
  readonly submitted = signal(false);
  readonly invalidEmail = signal(false);

  submit() {
    if (this.loading() || this.submitted()) {
      return;
    }

    if (!this.isValidEmail(this.email())) {
      this.invalidEmail.set(true);
      return;
    }

    this.invalidEmail.set(false);
    this.loading.set(true);

    this.auth.forgotPassword(this.email()).subscribe({
      next: () => this.onDone(),
      error: () => this.onDone(), // volontaire
    });
  }

  private onDone() {
    this.loading.set(false);
    this.submitted.set(true);
  }

  private isValidEmail(email: string): boolean {
    return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email);
  }
}
