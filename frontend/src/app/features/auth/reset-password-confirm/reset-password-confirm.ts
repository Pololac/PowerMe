import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { ResetPasswordForm } from '../reset-password-form/reset-password-form';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthLayout } from '../../../layout/auth-layout/auth-layout';
import { AuthService } from '../../../core/services/auth-service';

@Component({
  selector: 'app-reset-password-confirm',
  imports: [AuthLayout, ResetPasswordForm],
  templateUrl: './reset-password-confirm.html',
  styleUrl: './reset-password-confirm.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResetPasswordConfirm {
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  readonly successMessage = signal<string | null>(null);
  readonly errorMessage = signal<string | null>(null);
  readonly loading = signal(false);
  readonly token = this.route.snapshot.paramMap.get('token');

  constructor() {
    if (!this.token) {
      this.errorMessage.set('Lien invalide ou expiré.');
    }
  }

  handlePasswordSubmit(password: string) {
    if (!this.token || this.loading()) {
      return;
    }

    // Pour mpêcher la modification du formulaire après succès
    if (this.successMessage()) {
      return;
    }

    this.loading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.auth.resetPassword(this.token, password).subscribe({
      next: (res) => {
        this.loading.set(false);

        // Message renvoyé par le backend
        this.successMessage.set(res.message);
        setTimeout(() => {
          this.router.navigateByUrl('/login');
        }, 3000);
      },

      error: () => {
        this.errorMessage.set('Lien invalide ou expiré.');
        this.loading.set(false);
      },
    });
  }

  clearError(): void {
    this.errorMessage.set(null);
  }
}
