import {
  ChangeDetectionStrategy,
  Component,
  computed,
  inject,
  input,
  output,
  signal,
} from '@angular/core';
import { BookingPricingService } from './services/booking-pricing.service';
import { BookingSummary } from '../../core/models/dto/booking-summary.dto';
import { ChargingStationModal } from '../map/charging-station-modal/charging-station-modal';
import { PaymentModal } from './payment-modal/payment-modal';
import { PaymentService } from '../../core/services/payment.service';
import { BookingApiService } from './services/booking-api.service';
import { catchError, EMPTY, finalize, switchMap, tap } from 'rxjs';
import { LoggerService } from '../../core/error/logger-service';
import { AuthService } from '../../core/services/auth-service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-booking-flow',
  imports: [ChargingStationModal, PaymentModal],
  templateUrl: './booking-flow.html',
  styleUrl: './booking-flow.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BookingFlow {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly pricingService = inject(BookingPricingService);
  private readonly paymentService = inject(PaymentService);
  private readonly bookingApiService = inject(BookingApiService);
  private readonly logger = inject(LoggerService);

  readonly locationId = input.required<number>();

  readonly step = signal<'station' | 'payment'>('station');
  readonly bookingSummary = signal<BookingSummary | null>(null);

  readonly paymentLoading = signal(false);
  readonly paymentSuccess = signal(false);

  readonly paymentError = signal<string | null>(null);
  readonly bookingError = signal<string | null>(null);
  // Message unique pour l'UI
  readonly errorMessage = computed(() => this.paymentError() ?? this.bookingError());

  readonly flowClosed = output<void>();

  onStationConfirmed(data: {
    stationId: number;
    stationName: string;
    hourlyRate: number;
    date: string;
    slots: number[]; // ex: [18, 19, 20]
  }) {

    const pricing = this.pricingService.computePricing({
      hourlyRate: data.hourlyRate,
      slots: data.slots.length,
    });

    this.bookingSummary.set({
      stationId: data.stationId,
      stationName: data.stationName,
      hourlyRate: data.hourlyRate,
      date: data.date,
      slots: data.slots,
      ...pricing,
    });

    this.step.set('payment');
  }

  onBackToStation() {
    this.step.set('station');
  }

  onConfirmPayment(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/login'], {
        queryParams: { redirectUrl: '/map' },
      });
      return;
    }

    const summary = this.bookingSummary();
    if (!summary) return;

    this.paymentError.set(null);
    this.paymentSuccess.set(false);
    this.paymentLoading.set(true);

    this.paymentService
      .pay()
      .pipe(
        switchMap((result) => {
          if (result.status !== 'succeeded') {
            throw new Error('Payment failed');
          }

          return this.bookingApiService.createBooking({
            stationId: summary.stationId,
            date: summary.date,
            slots: summary.slots,
          });
        }),
        tap(() => {
          this.paymentSuccess.set(true);
          setTimeout(() => this.closeFlow(), 1500);
        }),
        catchError((err) => {
          this.logger.error('Payment succeeded but booking creation failed', err);
          this.paymentError.set('Une erreur est survenue lors de la réservation');
          return EMPTY;
        }),
        finalize(() => {
          this.paymentLoading.set(false);
        }),
      )
      .subscribe();
  }

  closeFlow() {
    this.step.set('station');
    this.flowClosed.emit();
  }
}
