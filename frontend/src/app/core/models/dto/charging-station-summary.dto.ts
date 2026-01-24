import { SocketType } from '../enums/socket-type.enum';
import { StationStatus } from '../enums/station-status.enum';

export interface ChargingStationSummaryDto {
  id: number;
  name: string;
  socketType: SocketType;
  powerKw: number;
  status: StationStatus;
  imagePath: string | null;
}
