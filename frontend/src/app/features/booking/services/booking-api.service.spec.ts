import { TestBed } from '@angular/core/testing';

import { BookingApiService } from './booking-api.service';

describe('BookingApiService', () => {
  let service: BookingApiService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookingApiService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
