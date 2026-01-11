import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { MapService } from '../services/map-service';
import { MapView } from '../map-view/map-view';
import { SearchLocation } from '../search-location/search-location';
import { ChargingLocationModal } from '../charging-location-modal/charging-location-modal';
import { ChargingStationModal } from '../charging-station-modal/charging-station-modal';

@Component({
  selector: 'app-map-page',
  imports: [MapView, SearchLocation, ChargingLocationModal, ChargingStationModal],
  providers: [MapService],
  templateUrl: './map-page.html',
  styleUrl: './map-page.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MapPage {
  private mapService = inject(MapService);

  onAddressSelected(lng: number, lat: number) {
    this.mapService.setSearchPosition([lng, lat]);
  }
}
