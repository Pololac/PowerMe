
import { inject, Injectable, signal } from '@angular/core';
import { ChargingLocationDetailDto } from '../../models/dto/charging-location-detail.dto';
import { ChargingStationSummaryDto } from '../../models/dto/charging-station-summary.dto';
import { ChargingLocationApi } from './charging-location.api';

@Injectable({
  providedIn: 'root',
})
export class ChargingLocationStore {
  private readonly api = inject(ChargingLocationApi);

  readonly selectedLocation = signal<ChargingLocationDetailDto | null>(null);
  readonly selectedStation = signal<ChargingStationSummaryDto | null>(null);
  readonly loading = signal(false);

  loadLocationDetail(id: number) {
    this.loading.set(true);

    this.api.getById(id).subscribe({
      next: (location) => this.selectedLocation.set(location),
      error: () => this.selectedLocation.set(null),
      complete: () => this.loading.set(false),
    });
  }

  selectStation(station: ChargingStationSummaryDto) {
    this.selectedStation.set(station);
  }

  clearStation() {
    this.selectedStation.set(null);
  }

  clear() {
    this.selectedLocation.set(null);
    this.clearStation();
  }
}
