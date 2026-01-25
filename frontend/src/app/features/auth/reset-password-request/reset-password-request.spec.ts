import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ResetPasswordRequest } from './reset-password-request';

describe('ResetPasswordRequest', () => {
  let component: ResetPasswordRequest;
  let fixture: ComponentFixture<ResetPasswordRequest>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ResetPasswordRequest]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ResetPasswordRequest);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
