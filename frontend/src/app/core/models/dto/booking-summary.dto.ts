export interface BookingSummary {
  stationId: number;
  stationName: string;
  date: string;
  slots: number[]; // Indexes des slots sélectionnés
  hourlyRate: number;
  durationHours: number; // Utile pr UI User
  serviceFee: number;
  total: number;
}
