import { ChangeDetectionStrategy, Component, inject, output } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginFormSubmit } from './login-form-submit';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-login-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login-form.html',
  styleUrl: './login-form.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginForm {
  private fb = inject(NonNullableFormBuilder);
  readonly formSubmit = output<LoginFormSubmit>();
  readonly formChange = output<void>();

  protected readonly form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64)]],
    rememberMe: this.fb.control(false),
  });

  constructor() {
    this.form.valueChanges.subscribe(() => {
      this.formChange.emit();
    });
  }

  // helper d'accès aux propriétés du formulaire
  get f() {
    return this.form.controls;
  }

  onSubmit() {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const { email, password, rememberMe } = this.form.getRawValue();
    this.formSubmit.emit({
      email,
      password,
      rememberMe,
    });
  }

  /**
   * Méthode utilisé par les tests pour assigner des valeurs au formulaire
   */
  get setValue() {
    return this.form.setValue.bind(this.form);
  }
}
