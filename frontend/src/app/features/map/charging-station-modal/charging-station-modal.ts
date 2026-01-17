import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ChargingStationStore } from './services/charging-station.store';
import { AvailabilityCalendar } from './availability-calendar/availability-calendar';
import { AvailabilitySlots } from './availability-slots/availability-slots';

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

  onDateSelected(date: string) {
    this.stationStore.loadAvailability(date);
  }

  close() {
    this.stationStore.closeStation();
  }
}
