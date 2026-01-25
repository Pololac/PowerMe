import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvailabilitySlots } from './availability-slots';

describe('AvailabilitySlots', () => {
  let component: AvailabilitySlots;
  let fixture: ComponentFixture<AvailabilitySlots>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvailabilitySlots]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AvailabilitySlots);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
