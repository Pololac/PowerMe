import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvailabilityCalendar } from './availability-calendar';

describe('AvailabilityCalendar', () => {
  let component: AvailabilityCalendar;
  let fixture: ComponentFixture<AvailabilityCalendar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvailabilityCalendar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AvailabilityCalendar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
