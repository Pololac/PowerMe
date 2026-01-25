import {
  ApplicationConfig,
  ErrorHandler,
  provideBrowserGlobalErrorListeners,
  provideZonelessChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { apiUrlInterceptor } from './core/interceptors/api-url-interceptor';
import { authInterceptor } from './core/interceptors/auth-interceptor';
import { GlobalErrorHandler } from './core/error/global-error-handler';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideZonelessChangeDetection(),
    provideRouter(routes),
    provideHttpClient(withFetch(), withInterceptors([apiUrlInterceptor, authInterceptor])),
    { provide: ErrorHandler, useClass: GlobalErrorHandler },
  ],
};
