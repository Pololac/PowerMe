import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserMenuTrigger } from './user-menu-trigger';

describe('UserMenuTrigger', () => {
  let component: UserMenuTrigger;
  let fixture: ComponentFixture<UserMenuTrigger>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserMenuTrigger],
    }).compileComponents();

    fixture = TestBed.createComponent(UserMenuTrigger);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
