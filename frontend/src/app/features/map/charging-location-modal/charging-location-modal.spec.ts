import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChargingLocationModal } from './charging-location-modal';

describe('ChargingLocationModal', () => {
  let component: ChargingLocationModal;
  let fixture: ComponentFixture<ChargingLocationModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChargingLocationModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChargingLocationModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
