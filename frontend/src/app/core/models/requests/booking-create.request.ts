export interface BookingCreateRequest {
  stationId: number;
  date: string; // YYYY-MM-DD
  slots: number[]; // ex: [18, 19, 20]
}
