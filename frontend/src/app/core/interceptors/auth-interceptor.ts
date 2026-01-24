import { HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, concatMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth-service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  const isLogin = req.url.includes('/auth/login');
  const isRefresh = req.url.includes('/auth/refresh');
  const isRegister = req.url.includes('/account/register');

  const token = localStorage.getItem('token') ?? sessionStorage.getItem('token');

  // Ne jamais intercepter les routes d'auth publiques
  if (isLogin || isRegister || isRefresh) {
    return next(req);
  }

  const authReq = token ? cloneWithBearer(req, token) : req;

  return next(authReq).pipe(
    catchError((err) => {
      // Si JWT expiré
      if (err.status === 401) {
        return authService.refreshToken().pipe(
          concatMap((newToken) => {
            localStorage.setItem('token', newToken);

            // Rejoue la requête originale
            return next(cloneWithBearer(req, newToken));
          }),
          catchError((refreshErr) => {
            // Refresh invalide → logout
            authService.logout();
            return throwError(() => refreshErr);
          }),
        );
      }

      return throwError(() => err);
    }),
  );
};

function cloneWithBearer(req: HttpRequest<unknown>, token: string) {
  return req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`,
    },
  });
}
