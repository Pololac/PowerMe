import { SocketType } from '../enums/socket-type.enum';
import { StationStatus } from '../enums/station-status.enum';

export interface ChargingStationDetailDto {
  id: number;
  name: string;
  socketType: SocketType;
  powerKw: number;
  status: StationStatus;
  imagePath: string | null;
  hourlyRate: number;
  active: boolean;
  /** Plage horaire journalière (optionnelle) */
  availableFrom?: string; // 'HH:mm'
  availableTo?: string; // 'HH:mm'
}
