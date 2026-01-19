import { ChangeDetectionStrategy, Component, inject, output } from '@angular/core';
import { ChargingStationStore } from './services/charging-station.store';
import { AvailabilityCalendar } from './availability-calendar/availability-calendar';
import { AvailabilitySlots } from './availability-slots/availability-slots';

export interface BookingPayload {
  stationId: number;
  stationName: string;
  hourlyRate: number;
  slots: number;
}

@Component({
  selector: 'app-charging-station-modal',
  imports: [AvailabilityCalendar, AvailabilitySlots],
  templateUrl: './charging-station-modal.html',
  styleUrl: './charging-station-modal.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChargingStationModal {
  readonly stationStore = inject(ChargingStationStore);

  readonly station = this.stationStore.station;
  readonly loading = this.stationStore.loading;
  readonly selectedSlotsCount = this.stationStore.selectedSlotsCount;

  readonly confirm = output<BookingPayload>();
  readonly closeModal = output<void>();

  onDateSelected(date: string) {
    this.stationStore.loadAvailability(date);
  }

  confirmReservation() {
    const station = this.station();
    if (!station) return;

    this.confirm.emit({
      stationId: station.id,
      stationName: station.name,
      hourlyRate: station.hourlyRate,
      slots: this.selectedSlotsCount(),
    });
  }

  close() {
    this.stationStore.closeStation();
    this.closeModal.emit();
  }
}
