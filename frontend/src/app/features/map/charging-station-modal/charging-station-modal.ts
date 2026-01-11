import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { ChargingLocation } from '../../../core/services/charging-location';

@Component({
  selector: 'app-charging-station-modal',
  imports: [],
  templateUrl: './charging-station-modal.html',
  styleUrl: './charging-station-modal.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ChargingStationModal {
  private service = inject(ChargingLocation);

  readonly station = this.service.selectedStation;

  close() {
    this.service.clearStation();
  }
}
