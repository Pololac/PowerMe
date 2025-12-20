import { NonNullableFormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { output } from '@angular/core';
import { confirmPasswordValidator } from '../../../shared/validators/confirm-password.validator';

@Component({
  selector: 'app-reset-password-form',
  imports: [ReactiveFormsModule],
  templateUrl: './reset-password-form.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ResetPasswordForm {
  private fb = inject(NonNullableFormBuilder);

  readonly loading = input<boolean>(false);
  readonly formSubmit = output<string>();
  readonly formChange = output<void>();

  protected readonly form = this.fb.group(
    {
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

    this.formSubmit.emit(this.form.getRawValue().password);
  }

  // pour les tests
  get setValue() {
    return this.form.setValue.bind(this.form);
  }
}
