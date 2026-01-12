import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { Icon } from '../../../shared/components/icon/icon';
import { CommonModule } from '@angular/common';
import { ChargingStationSummaryDto } from '../../../core/models/dto/charging-station-summary.dto';
import { ChargingLocationStore } from '../../../core/services/charging-location/charging-location.store';
import { SOCKET_TYPE_ICON } from '../../../shared/ui/ui-mapping/socket-type-icon';
import { SOCKET_TYPE_LABEL } from '../../../shared/ui/ui-mapping/socket-type-label';
import { STATION_STATUS_UI } from '../../../shared/ui/ui-mapping/station-status-ui';

@Component({
  selector: 'app-charging-location-modal',
  imports: [Icon, CommonModule],
  templateUrl: './charging-location-modal.html',
  styleUrl: './charging-location-modal.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChargingLocationModal {
  private readonly locationStore = inject(ChargingLocationStore);

  // Références à des signaux définis dans le service
  readonly location = this.locationStore.selectedLocation;
  readonly loading = this.locationStore.loading;

  readonly SOCKET_TYPE_ICON = SOCKET_TYPE_ICON;
  readonly SOCKET_TYPE_LABEL = SOCKET_TYPE_LABEL;
  readonly STATION_STATUS_UI = STATION_STATUS_UI;

  openStation(station: ChargingStationSummaryDto) {
    this.locationStore.selectStation(station);
  }
  close() {
    this.locationStore.clear();
  }
}
