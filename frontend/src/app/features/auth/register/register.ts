import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { AuthLayout } from '../../../layout/auth-layout/auth-layout';
import { Router, RouterLink } from '@angular/router';
import { RegisterFormSubmit } from '../register-form/register-form-submit';
import { RegisterRequest } from '../../../core/models/requests/register.request';
import { RegisterForm } from '../register-form/register-form';
import { AuthService } from '../../../core/services/auth-service';

@Component({
  selector: 'app-register',
  imports: [AuthLayout, RegisterForm, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Register {
  private auth = inject(AuthService);
  private router = inject(Router);

  readonly successMessage = signal<string | null>(null);
  readonly errorMessage = signal<string | null>(null);
  readonly loading = signal(false);

  handleSubmit(formData: RegisterFormSubmit): void {
    this.loading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const request: RegisterRequest = {
      email: formData.email,
      password: formData.password,
    };

    this.auth.register(request).subscribe({
      next: (res) => {
        this.loading.set(false);

        // Message renvoyé par le backend
        this.successMessage.set(res.message);

        // UX : redirection différée vers login
        setTimeout(() => {
          this.router.navigateByUrl('/login');
        }, 5000);
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMessage.set(err?.error?.detail ?? 'Une erreur est survenue. Merci de réessayer.');
      },
    });
  }

  clearError(): void {
    this.errorMessage.set(null);
  }
}
