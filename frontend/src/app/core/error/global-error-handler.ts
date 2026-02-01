import { ErrorHandler, inject, Injectable, Injector } from '@angular/core';
import { LoggerService } from './logger-service';
import { HttpErrorResponse } from '@angular/common/http';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  injector = inject(Injector);

  handleError(error: unknown): void {
    const logger = this.injector.get(LoggerService); // Lazy injection : uniquement qd erreur survient

    // Erreurs HTTP (API)
    if (error instanceof HttpErrorResponse) {
      logger.error(
        `HTTP error ${error.status} on ${error.url ?? 'unknown URL'}`,
        error.error ?? error.message
      );
      return;
    }

    // Erreurs JS / Angular
    if (error instanceof Error) {
      logger.error(error.message, error.stack);
      return;
    }

    // Erreur inconnue
    logger.error('Unknown front-end error', error);
  }
}
