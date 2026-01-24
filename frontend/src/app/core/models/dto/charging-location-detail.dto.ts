import { ChargingStationSummaryDto } from './charging-station-summary.dto';

export interface ChargingLocationDetailDto {
  id: number;
  name: string;
  address: string;
  latitude: number;
  longitude: number;
  imagePath: string | null;
  stations: ChargingStationSummaryDto[];
  stationsCount: number;
}
