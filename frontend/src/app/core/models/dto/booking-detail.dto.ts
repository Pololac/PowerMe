export interface BookingDetailDto {
  id: number;
  startTime: string;
  endTime: string;
  totalPrice: number;
  bookingStatus: string;
  stationName: string;
  stationAddress: string;
  hourlyRate: number;
  createdAt: string;
}
