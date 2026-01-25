import { StationStatus } from '../../../core/models/enums/station-status.enum';

export const STATION_STATUS_UI: Record<
  StationStatus,
  {
    label: string;
    textClass: string;
    borderClass: string;
  }
> = {
  [StationStatus.AVAILABLE]: {
    label: 'Disponible',
    textClass: 'text-mint-600',
    borderClass: 'bg-mint-500',
  },
  [StationStatus.OCCUPIED]: {
    label: 'Occupée',
    textClass: 'text-blue-400',
    borderClass: 'bg-blue-400',
  },
  [StationStatus.UNAVAILABLE]: {
    label: 'Indisponible',
    textClass: 'text-neutral-grey3',
    borderClass: 'bg-neutral-grey3',
  },
};
