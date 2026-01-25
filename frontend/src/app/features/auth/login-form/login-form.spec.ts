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
    expect(debugElement.query(By.css('input[type="email"]'))).toBeTruthy();
    expect(debugElement.query(By.css('input[type="password"]'))).toBeTruthy();
  });

  it('should display validation errors on submit', () => {
    component.onSubmit();
    fixture.detectChanges();

    const errors = fixture.debugElement.queryAll(By.css('.form-error'));
    expect(errors.length).toBe(2);
  });

  it('should trigger output if inputs are valid via public API', () => {
    spyOn(component.formSubmit, 'emit');

    component.setValue({
      email: 'bloup@test.com',
      password: '12345678',
      rememberMe: true,
    });

    fixture.debugElement.query(By.css('form')).triggerEventHandler('ngSubmit');

    expect(component.formSubmit.emit).toHaveBeenCalledWith({
      email: 'bloup@test.com',
      password: '12345678',
      rememberMe: true,
    });
  });
});
