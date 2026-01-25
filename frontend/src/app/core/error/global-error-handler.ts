import { ErrorHandler, Injectable } from '@angular/core';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  handleError(error: unknown): void {
    // Ici, logging centralisé (mock / minimal)
    console.error('Global error:', error);
  }
}
