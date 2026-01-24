
import { inject, Injectable, signal } from '@angular/core';
import { ChargingLocationDetailDto } from '../../../../core/models/dto/charging-location-detail.dto';
import { ChargingLocationApi } from './charging-location.api';

@Injectable({
  providedIn: 'root',
})
export class ChargingLocationStore {
  private readonly api = inject(ChargingLocationApi);

  readonly selectedLocation = signal<ChargingLocationDetailDto | null>(null);
  readonly loading = signal(false);

  loadLocationDetail(id: number) {
    this.loading.set(true);

    this.api.getById(id).subscribe({
      next: (location) => this.selectedLocation.set(location),
      error: () => this.selectedLocation.set(null),
      complete: () => this.loading.set(false),
    });
  }

  clearLocation() {
    this.selectedLocation.set(null);
  }
}
