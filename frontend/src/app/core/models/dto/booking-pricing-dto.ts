export interface BookingPricingInput {
  hourlyRate: number;
  slots: number; // 1 slot = 30 min
}

export interface BookingPricingResult {
  durationHours: number;
  serviceFee: number;
  total: number;
}
