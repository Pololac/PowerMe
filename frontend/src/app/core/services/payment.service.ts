import { Injectable } from '@angular/core';
import { PaymentResponseDto } from '../models/dto/payment-response.dto';
import { PaymentRequest } from '../models/requests/payment.request';

@Injectable({
  providedIn: 'root',
})
export class PaymentService {
  pay(request: PaymentRequest): Promise<PaymentResponseDto> {
    return new Promise((resolve) => {
      setTimeout(() => {
        resolve({
          paymentId: crypto.randomUUID(),
          status: 'succeeded',
        });
      }, 2000); // latence réseau simulée
    });
  }
}
