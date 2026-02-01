import { ChangeDetectionStrategy, Component, computed, inject, input, output } from '@angular/core';
import { BookingSummary } from '../../../core/models/dto/booking-summary.dto';
import { CommonModule } from '@angular/common';
import { ChargingLocationStore } from '../../map/charging-location-modal/services/charging-location.store';

@Component({
  selector: 'app-payment-modal',
  imports: [CommonModule],
  templateUrl: './payment-modal.html',
  styleUrl: './payment-modal.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaymentModal {
  readonly locationStore = inject(ChargingLocationStore);

  readonly summary = input.required<BookingSummary>();
  readonly loading = input<boolean>(false);
  readonly success = input<boolean>(false);
  readonly error = input<string | null>(null);

  readonly address = computed(() => this.locationStore.selectedLocation()?.address ?? '');

  readonly confirm = output<void>();
  readonly back = output<void>();

  onConfirm() {
    this.confirm.emit();
  }

  onBack() {
    this.back.emit();
  }
}
