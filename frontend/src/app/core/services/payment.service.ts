import { Injectable } from '@angular/core';
import { PaymentResponseDto } from '../models/dto/payment-response.dto';
import { delay, Observable, of } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class PaymentService {
  pay(): Observable<PaymentResponseDto> {
    const response: PaymentResponseDto = {
      paymentId: crypto.randomUUID(),
      status: 'succeeded',
    };

    return of(response).pipe(delay(2000));
  }
}
