import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoggerService {
  error(message: string, error?: unknown): void {
    console.error('[FRONT][ERROR]', message, error);
  }

  warn(message: string, data?: unknown): void {
    console.warn('[FRONT][WARN]', message, data);
  }

  info(message: string, data?: unknown): void {
    console.info('[FRONT][INFO]', message, data);
  }
}
