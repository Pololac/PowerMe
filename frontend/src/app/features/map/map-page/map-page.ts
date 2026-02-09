import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { MapService } from '../services/map-service';
import { MapView } from '../map-view/map-view';
import { SearchLocation } from '../search-location/search-location';
import { ChargingLocationModal } from '../charging-location-modal/charging-location-modal';
import { BookingFlow } from '../../booking/booking-flow';
import { ChargingStationStore } from '../charging-station-modal/services/charging-station.store';

@Component({
  selector: 'app-map-page',
  imports: [MapView, SearchLocation, ChargingLocationModal, BookingFlow],
  providers: [MapService],
  templateUrl: './map-page.html',
  styleUrl: './map-page.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MapPage {
  private readonly mapService = inject(MapService);
  readonly stationStore = inject(ChargingStationStore);

  readonly bookingOpen = signal(false);
  readonly selectedLocationId = signal<number | null>(null);

  onAddressSelected(lng: number, lat: number) {
    this.mapService.setSearchPosition([lng, lat]);
  }

  onLocationSelected(locationId: number) {
    this.selectedLocationId.set(locationId);
  }

  onStationSelected(stationId: number) {
    this.stationStore.openStation(stationId);
    this.bookingOpen.set(true);
  }

  closeBooking() {
    this.bookingOpen.set(false);
    this.stationStore.resetSelection();
  }
}
