import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewSlider } from './review-slider';

describe('ReviewSlider', () => {
  let component: ReviewSlider;
  let fixture: ComponentFixture<ReviewSlider>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ReviewSlider]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewSlider);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
