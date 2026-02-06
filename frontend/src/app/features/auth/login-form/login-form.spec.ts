import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoginForm } from './login-form';
import { By } from '@angular/platform-browser';
import { provideRouter } from '@angular/router';

describe('LoginForm', () => {
  let component: LoginForm;
  let fixture: ComponentFixture<LoginForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginForm],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(LoginForm);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create a form with credentials inputs', () => {
    const debugElement = fixture.debugElement;

    expect(component).toBeTruthy();

    // Vérifie les champs Email, Password et RememberMe
    const inputEmail = debugElement.query(By.css('input[type="email"]'));
    expect(inputEmail).toBeTruthy();
    const inputPassword = debugElement.query(By.css('input[type="password"]'));
    expect(inputPassword).toBeTruthy();
    const inputRememberMe = debugElement.query(By.css('input[type="checkbox"]'));
    expect(inputRememberMe).toBeTruthy();
  });

  it('should display validation errors on submit', () => {
    component.onSubmit();
    fixture.detectChanges();

    // Vérifie que la soumission d'un formulaire vide génère l'apparition de 2 messages d'erreurs
    const errors = fixture.debugElement.queryAll(By.css('.form-error'));
    expect(errors.length).toBe(2);
  });

  it('should trigger output if inputs are valid', () => {
    spyOn(component.formSubmit, 'emit');

    component.setValue({
      email: 'bloup@test.com',
      password: '12345678',
      rememberMe: true,
    });

    fixture.debugElement.query(By.css('form')).triggerEventHandler('ngSubmit');

    // Vérifie que le formulaire émet les valeurs saisies
    expect(component.formSubmit.emit).toHaveBeenCalledWith({
      email: 'bloup@test.com',
      password: '12345678',
      rememberMe: true,
    });
  });

  // Vérifie que le formulaire n'émet rien s'il est invalide (vide)
  it('should NOT emit output if form is invalid', () => {
    spyOn(component.formSubmit, 'emit');

    component.onSubmit();

    expect(component.formSubmit.emit).not.toHaveBeenCalled();
  });
});
