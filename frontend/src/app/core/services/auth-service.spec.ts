import { TestBed } from '@angular/core/testing';

import { AuthService } from './auth-service';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter, Router } from '@angular/router';
import { User } from '../models/domain/user.model';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting(),
        provideRouter([]),
      ],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
    sessionStorage.clear();
  });

  it('should login and map response to token + user', () => {
    const fakeUser = { id: 1, email: 'test@test.com' } as User;

    // Vérification que le mapping retourne bien l'objet métier front attendu { token, user }
    service.login({ email: 'test@test.com', password: '12345678' }).subscribe((result) => {
      expect(result.token).toBe('fake-token');
      expect(result.user).toEqual(fakeUser);
    });

    // Vérification URL & méthode
    const req = httpMock.expectOne('/auth/login');
    expect(req.request.method).toBe('POST');

    // Simule le mapping côté front de la réponse
    req.flush({
      accessToken: 'fake-token',
      user: fakeUser,
    });
  });

  it('should store session in sessionStorage when rememberMe is false', () => {
    const fakeUser = { id: 3, email: 'session@test.com' } as User;

    service.setSession('token456', fakeUser, false);

    expect(sessionStorage.getItem('token')).toBe('token456');
    expect(JSON.parse(sessionStorage.getItem('user')!)).toEqual(fakeUser);
    expect(service.user()).toEqual(fakeUser);
  });

  it('should clear session and navigate on logout', () => {
    const router = TestBed.inject(Router);
    const httpMock = TestBed.inject(HttpTestingController);

    spyOn(router, 'navigateByUrl');

    service.setSession('token', { id: 1 } as User, false);

    service.logout().subscribe();

    const req = httpMock.expectOne('/auth/logout');
    req.flush({}); // simule succès backend

    expect(sessionStorage.getItem('token')).toBeNull();
    expect(sessionStorage.getItem('user')).toBeNull();
    expect(service.user()).toBeNull();
    expect(router.navigateByUrl).toHaveBeenCalledWith('/');
  });
});
