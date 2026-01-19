import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookingFlow } from './booking-flow';

describe('BookingFlow', () => {
  let component: BookingFlow;
  let fixture: ComponentFixture<BookingFlow>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookingFlow]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookingFlow);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
