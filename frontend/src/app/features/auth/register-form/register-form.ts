import { ChangeDetectionStrategy, Component, inject, input, output } from '@angular/core';
import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RegisterFormSubmit } from './register-form-submit';
import { confirmPasswordValidator } from '../../../shared/validators/confirm-password.validator';

@Component({
  selector: 'app-register-form',
  imports: [ReactiveFormsModule],
  templateUrl: './register-form.html',
  styleUrl: './register-form.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RegisterForm {
  private fb = inject(NonNullableFormBuilder);

  readonly loading = input<boolean>(false);
  readonly formSubmit = output<RegisterFormSubmit>();
  readonly formChange = output<void>();

  protected readonly form = this.fb.group(
    {
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8), Validators.maxLength(64)]],
      confirmPassword: ['', [Validators.required]],
    },
    {
      validators: confirmPasswordValidator,
    },
  );

  constructor() {
    this.form.valueChanges.subscribe(() => {
      this.formChange.emit();
    });
  }

  get f() {
    return this.form.controls;
  }

  onSubmit() {
    if (this.form.invalid || this.loading()) {
      this.form.markAllAsDirty();
      return;
    }

    const { email, password } = this.form.getRawValue();
    this.formSubmit.emit({ email, password });
  }

  // pour les tests
  get setValue() {
    return this.form.setValue.bind(this.form);
  }
}
