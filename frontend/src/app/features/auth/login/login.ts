import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { LoginForm } from '../login-form/login-form';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { LoginFormSubmit } from '../login-form/login-form-submit';
import { AuthLayout } from '../../../layout/auth-layout/auth-layout';
import { AuthService } from '../../../core/services/auth/auth-service';

@Component({
  selector: 'app-login',
  imports: [AuthLayout, LoginForm, RouterLink],
  templateUrl: './login.html',
  styleUrl: './login.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class Login {
  private auth = inject(AuthService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  readonly errorMessage = signal<string | null>(null);
  readonly loading = signal(false);

  handleSubmit({ email, password, rememberMe }: LoginFormSubmit) {
    this.loading.set(true);
    this.errorMessage.set(null);

    this.auth.login({ email, password }).subscribe({
      next: ({ token, user }) => {
        this.loading.set(false);
        this.auth.setSession(token, user, rememberMe);
        console.log('Auth user signal', this.auth.user());

        // Redirection
        const redirectUrl = this.route.snapshot.queryParamMap.get('redirectUrl') ?? '/';

        this.router.navigateByUrl(redirectUrl);
      },

      error: (err) => {
        this.loading.set(false);
        const backendMessage = err?.error?.detail ?? err?.error?.message ?? '';

        if (backendMessage === 'Account not activated') {
          this.errorMessage.set('Votre compte n’est pas encore activé. Vérifiez votre email.');
        } else if (err.status === 401) {
          this.errorMessage.set('Email ou mot de passe incorrect.');
        } else {
          this.errorMessage.set('Une erreur est survenue. Merci de réessayer.');
        }
      },
    });
  }

  clearError(): void {
    this.errorMessage.set(null);
  }
}
