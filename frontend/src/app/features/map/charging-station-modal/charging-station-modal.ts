import { ChangeDetectionStrategy, Component, inject, output } from '@angular/core';
import { ChargingStationStore } from './services/charging-station.store';
import { AvailabilityCalendar } from './availability-calendar/availability-calendar';
import { AvailabilitySlots } from './availability-slots/availability-slots';

export interface BookingPayload {
  stationId: number;
  stationName: string;
  hourlyRate: number;
  date: string;
  slots: number[]; // slotIndex[]
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
  readonly selectedSlots = this.stationStore.selectedSlots;
  readonly selectedSlotsCount = this.stationStore.selectedSlotsCount;

  readonly confirm = output<BookingPayload>();
  readonly closeModal = output<void>();

  onDateSelected(date: string) {
    this.stationStore.loadAvailability(date);
  }

  confirmStationBooking() {
    const station = this.station();
    const date = this.stationStore.date();
    const slots = this.stationStore.selectedSlots();

    if (!station || !date || slots.length === 0) return;

    const slotIndexes = slots.map((slot) => slot.index);

    this.confirm.emit({
      stationId: station.id,
      stationName: station.name,
      hourlyRate: station.hourlyRate,
      date,
      slots: slotIndexes,
    });
  }

  close() {
    this.stationStore.closeStation();
    this.closeModal.emit();
  }
}
