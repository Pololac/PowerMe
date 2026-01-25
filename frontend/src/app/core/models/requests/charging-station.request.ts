import { ChargingPower } from '../enums/charging-power.enum';
import { SocketType } from '../enums/socket-type.enum';

export interface ChargingStationRequest {
  name: string;
  socketType: SocketType;
  power: ChargingPower;
  hourlyRate: number; // number côté front
  active: boolean;
  availableFrom: string | null; // "HH:mm"
  availableTo: string | null; // "HH:mm"
}
