import { ChangeDetectionStrategy, Component, inject, OnInit, signal } from '@angular/core';
import { MapService } from '../services/map-service';
import { MapView } from '../map-view/map-view';
import { SearchLocation } from '../search-location/search-location';
import { ChargingLocationModal } from '../charging-location-modal/charging-location-modal';
import { BookingFlow } from '../../booking/booking-flow';
import { ChargingStationStore } from '../charging-station-modal/services/charging-station.store';
import { AuthService } from '../../../core/services/auth-service';
import { Router } from '@angular/router';
import { BookingIntentService } from '../../booking/services/booking-intent.service';

@Component({
  selector: 'app-map-page',
  imports: [MapView, SearchLocation, ChargingLocationModal, BookingFlow],
  providers: [MapService],
  templateUrl: './map-page.html',
  styleUrl: './map-page.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MapPage implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly bookingIntent = inject(BookingIntentService);

  private readonly mapService = inject(MapService);
  readonly stationStore = inject(ChargingStationStore);

  readonly bookingOpen = signal(false);
  readonly selectedLocationId = signal<number | null>(null);

  ngOnInit() {
    const pendingId = this.bookingIntent.consume();

    if (pendingId !== null) {
      this.selectedLocationId.set(pendingId);
      this.bookingOpen.set(true);
    }
  }

  onAddressSelected(lng: number, lat: number) {
    this.mapService.setSearchPosition([lng, lat]);
  }

  onLocationSelected(locationId: number) {
    this.selectedLocationId.set(locationId);
  }

  onStationSelected(stationId: number) {
    this.stationStore.openStation(stationId);
    this.openBooking();
  }

  openBooking() {
    const locationId = this.selectedLocationId();
    if (!locationId) return;

    if (!this.authService.isAuthenticated()) {
      this.bookingIntent.set(locationId);

      this.router.navigate(['/login'], {
        queryParams: { redirectUrl: '/map' },
      });
      return;
    }

    this.bookingOpen.set(true);
  }

  closeBooking() {
    this.bookingOpen.set(false);
    this.stationStore.resetSelection();
  }
}
