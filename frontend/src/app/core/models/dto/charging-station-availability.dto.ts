export interface ChargingStationAvailabilityDto {
  date: string; // YYYY-MM-DD
  slots: ChargingStationTimeSlotDto[];
}

export interface ChargingStationTimeSlotDto {
  start: string; // HH:mm
  end: string; // HH:mm
  available: boolean;
}
