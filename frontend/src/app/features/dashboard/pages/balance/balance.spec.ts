import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Balance } from './balance';

describe('Balance', () => {
  let component: Balance;
  let fixture: ComponentFixture<Balance>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Balance]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Balance);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
