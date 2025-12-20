import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

/**
 *
 * Interceptor pour rajouter l'url du backend avant chaque requête
 * qui ne commence pas par http (par exemple si on souhaite requêter une autre api)
 * et faire d'autres modifications de requete globale si besoin
 */
export const apiUrlInterceptor: HttpInterceptorFn = (req, next) => {
  const isAbsolute = /^https?:\/\//i.test(req.url);
  const isAsset = req.url.includes('/assets/');
  const isDataLike = /^(data:|blob:)/i.test(req.url);

  if (isAbsolute || isAsset || isDataLike) {
    return next(req);
  }

  const url = `${environment.apiBaseUrl}${req.url}`;

  return next(req.clone({ url }));
};
