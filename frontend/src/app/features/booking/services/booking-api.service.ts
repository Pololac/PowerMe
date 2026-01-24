import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BookingCreateRequest } from '../../../core/models/requests/booking-create.request';
import { BookingDetailDto } from '../../../core/models/dto/booking-detail.dto';

@Injectable({
  providedIn: 'root',
})
export class BookingApiService {
  private readonly http = inject(HttpClient);

  createBooking(request: BookingCreateRequest) {
    return this.http.post<BookingDetailDto>('/bookings', request);
  }
}
