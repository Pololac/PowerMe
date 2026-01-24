export interface ChargingStationAvailabilityDto {
  date: string; // YYYY-MM-DD
  slots: ChargingStationTimeSlotDto[];
}

export interface ChargingStationTimeSlotDto {
  index: number; // clé technique
  start: string; // HH:mm
  end: string; // HH:mm
  available: boolean;
}
