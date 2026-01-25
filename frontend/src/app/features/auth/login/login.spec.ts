import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Login } from './login';
import { AuthService } from '../../../core/services/auth-service';
import { of, throwError } from 'rxjs';
import { User } from '../../../core/models/domain/user.model';

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let authServiceSpy: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [{ provide: AuthService, useValue: authServiceSpy }],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the login page', () => {
    expect(component).toBeTruthy();
  });

  it('should call AuthService.login on form submit', () => {
    authServiceSpy.login.and.returnValue(
      of({
        token: 'fake-token',
        user: {} as User,
      }),
    );

    component.handleSubmit({
      email: 'test@test.com',
      password: 'password123',
      rememberMe: false,
    });

    expect(authServiceSpy.login).toHaveBeenCalled();
  });

  it('should set errorMessage on login error', () => {
    authServiceSpy.login.and.returnValue(throwError(() => new Error('Invalid credentials')));

    component.handleSubmit({
      email: 'wrong@test.com',
      password: 'wrongpass',
      rememberMe: false,
    });

    expect(component.errorMessage()).toBeTruthy();
  });
});
