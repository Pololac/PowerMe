import { ChangeDetectionStrategy, Component, inject, input, output, signal } from '@angular/core';
import { BookingPricingService } from './services/booking-pricing.service';
import { BookingSummary } from '../../core/models/dto/booking-summary.dto';
import { ChargingStationModal } from '../map/charging-station-modal/charging-station-modal';
import { PaymentModal } from './payment-modal/payment-modal';
import { PaymentService } from '../../core/services/payment.service';

@Component({
  selector: 'app-booking-flow',
  imports: [ChargingStationModal, PaymentModal],
  templateUrl: './booking-flow.html',
  styleUrl: './booking-flow.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BookingFlow {
  private readonly pricingService = inject(BookingPricingService);
  private readonly paymentService = inject(PaymentService);

  readonly locationId = input.required<number>();

  readonly step = signal<'station' | 'payment'>('station');
  readonly bookingSummary = signal<BookingSummary | null>(null);
  readonly paymentLoading = signal(false);
  readonly paymentSuccess = signal(false);

  readonly flowClosed = output<void>();

  onStationConfirmed(data: { stationName: string; hourlyRate: number; slots: number }) {
    const pricing = this.pricingService.computePricing({
      hourlyRate: data.hourlyRate,
      slots: data.slots,
    });

    this.bookingSummary.set({
      stationName: data.stationName,
      hourlyRate: data.hourlyRate,
      ...pricing,
    });

    this.step.set('payment');
  }

  onBackToStation() {
    this.step.set('station');
  }

  async onConfirmPayment(): Promise<void> {
    const summary = this.bookingSummary();
    if (!summary) return;

    this.paymentLoading.set(true);

    try {
      const result = await this.paymentService.pay({
        amount: summary.total,
        currency: 'EUR',
        description: `Recharge ${summary.stationName}`,
      });

      if (result.status === 'succeeded') {
        this.paymentSuccess.set(true);

        setTimeout(() => {
          this.closeFlow();
        }, 1500);
      }
    } finally {
      this.paymentLoading.set(false);
    }
  }

  closeFlow() {
    this.step.set('station');
    this.flowClosed.emit();
  }
}
