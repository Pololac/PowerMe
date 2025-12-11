import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LandingNavbar } from './landing-navbar';

describe('LandingNavbar', () => {
  let component: LandingNavbar;
  let fixture: ComponentFixture<LandingNavbar>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LandingNavbar]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LandingNavbar);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
