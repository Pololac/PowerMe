import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChargingStationModal } from './charging-station-modal';

describe('ChargingStationModal', () => {
  let component: ChargingStationModal;
  let fixture: ComponentFixture<ChargingStationModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChargingStationModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChargingStationModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
