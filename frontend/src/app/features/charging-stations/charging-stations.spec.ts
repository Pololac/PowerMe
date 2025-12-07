import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChargingStations } from './charging-stations';

describe('ChargingStations', () => {
  let component: ChargingStations;
  let fixture: ComponentFixture<ChargingStations>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChargingStations]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ChargingStations);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
