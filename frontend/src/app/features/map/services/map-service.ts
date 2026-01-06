import { HttpClient } from '@angular/common/http';
import { inject, Injectable, signal } from '@angular/core';
import { ChargingLocationMapDto } from '../../../core/models/dto/charging-location-map.dto';

export type LngLat = [number, number];
export interface Bounds {
  north: number;
  south: number;
  east: number;
  west: number;
}

@Injectable({
  providedIn: 'root',
})
export class MapService {
  readonly center = signal<LngLat | null>(null);
  readonly zoom = signal<number | null>(null);

  readonly userPosition = signal<LngLat | null>(null);
  readonly searchPosition = signal<LngLat | null>(null);

  readonly locations = signal<ChargingLocationMapDto[]>([]);

  private http = inject(HttpClient);

  setCenter(coords: LngLat, zoom?: number) {
    this.center.set(coords);
    if (zoom !== undefined) {
      this.zoom.set(zoom);
    }
  }

  setUserPosition(coords: LngLat) {
    this.userPosition.set(coords);
    this.setCenter(coords, 13);
  }

  setSearchPosition(coords: LngLat) {
    this.searchPosition.set(coords);
    this.setCenter(coords, 14);
  }

  resetSearch() {
    this.searchPosition.set(null);
  }

  loadLocationsInBounds(bounds: Bounds) {
    this.http
      .get<ChargingLocationMapDto[]>('/charging-locations/bounds', {
        params: bounds as any,
      })
      .subscribe((data) => {
        this.locations.set(data);
      });
  }
}
