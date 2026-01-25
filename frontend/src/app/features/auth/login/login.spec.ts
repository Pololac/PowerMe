import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Login } from './login';
import { AuthService } from '../../../core/services/auth-service';
import { ActivatedRoute, provideRouter, Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { User } from '../../../core/models/domain/user.model';

/**
 * Helper pour mocker ActivatedRoute.snapshot.queryParamMap
 */
function makeActivatedRouteMock(redirectUrl: string | null) {
  return {
    snapshot: {
      queryParamMap: {
        get: (key: string) => (key === 'redirectUrl' ? redirectUrl : null),
      },
    },
  } as unknown as ActivatedRoute;
}

describe('Login', () => {
  let component: Login;
  let fixture: ComponentFixture<Login>;
  let router: Router;

  const authMock = {
    login: jasmine.createSpy('login'),
    setSession: jasmine.createSpy('setSession'),
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Login],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authMock }, // mock AuthService
        { provide: ActivatedRoute, useValue: makeActivatedRouteMock(null) }, // pas de redirectUrl par défaut
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(Login);
    component = fixture.componentInstance;

    router = TestBed.inject(Router);
    spyOn(router, 'navigateByUrl'); // On spy ici pour éviter une vraie navigation

    fixture.detectChanges();
  });

  afterEach(() => {
    authMock.login.calls.reset();
    authMock.setSession.calls.reset();
  });

  it('should call AuthService.login with email and password', () => {
    authMock.login.and.returnValue(of({ token: 'token', user: { id: 1 } as User }));

    component.handleSubmit({
      email: 'test@test.com',
      password: '12345678',
      rememberMe: true,
    });

    expect(authMock.login).toHaveBeenCalledWith({
      email: 'test@test.com',
      password: '12345678',
    });
  });

  it('should set session and redirect to /dashboard on success', () => {
    const fakeUser = { id: 1 } as User;

    authMock.login.and.returnValue(of({ token: 'token123', user: fakeUser }));

    component.handleSubmit({
      email: 'a@b.com',
      password: '12345678',
      rememberMe: true,
    });

    expect(authMock.setSession).toHaveBeenCalledWith('token123', fakeUser, true);

    expect(router.navigateByUrl).toHaveBeenCalledWith('/dashboard');
  });

  it('should display error message on 401 error', () => {
    authMock.login.and.returnValue(throwError(() => ({ status: 401 })));

    component.handleSubmit({
      email: 'wrong@test.com',
      password: 'badpass',
      rememberMe: false,
    });

    expect(component.errorMessage()).toBe('Email ou mot de passe incorrect.');
  });

  it('should display specific message when account is not activated', () => {
    authMock.login.and.returnValue(
      throwError(() => ({
        error: { detail: 'Account not activated' },
      })),
    );

    component.handleSubmit({
      email: 'test@test.com',
      password: '12345678',
      rememberMe: false,
    });

    expect(component.errorMessage()).toBe(
      'Votre compte n’est pas encore activé. Vérifiez votre email.',
    );
  });
});
