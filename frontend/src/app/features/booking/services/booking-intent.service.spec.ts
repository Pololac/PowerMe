import { TestBed } from '@angular/core/testing';

import { BookingIntentService } from './booking-intent.service';

describe('BookingIntentService', () => {
  let service: BookingIntentService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookingIntentService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
