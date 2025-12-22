import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserMenuDropdown } from './user-menu-dropdown';

describe('UserMenuDropdown', () => {
  let component: UserMenuDropdown;
  let fixture: ComponentFixture<UserMenuDropdown>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserMenuDropdown]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserMenuDropdown);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
