import { HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, concatMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth-service';
import { environment } from '../../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);

  const token = localStorage.getItem('token') ?? sessionStorage.getItem('token');

  // Ne jamais intercepter si :
  //  - requête externe (MapTiler, Stripe, etc.)
  //  - routes publiques auth
  //  - pas de token
  const isBackendRequest = req.url.startsWith(environment.apiBaseUrl);
  const isPublicAuthRoute =
    req.url.includes('/auth/login') ||
    req.url.includes('/auth/refresh') ||
    req.url.includes('/account/register');

  if (!token || !isBackendRequest || isPublicAuthRoute) {
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
