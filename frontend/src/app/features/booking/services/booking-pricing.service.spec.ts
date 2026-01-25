import { TestBed } from '@angular/core/testing';

import { BookingPricingService } from './booking-pricing.service';

describe('BookingPricingService', () => {
  let service: BookingPricingService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookingPricingService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
