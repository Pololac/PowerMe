import { TestBed } from '@angular/core/testing';

import { ChargingLocation } from './charging-location';

describe('ChargingLocation', () => {
  let service: ChargingLocation;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ChargingLocation);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
