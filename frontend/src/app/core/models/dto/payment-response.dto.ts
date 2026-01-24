export interface PaymentResponseDto {
  paymentId: string;
  status: 'succeeded' | 'failed';
}
