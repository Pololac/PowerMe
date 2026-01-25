import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SearchLocation } from './search-location';

describe('SearchLocation', () => {
  let component: SearchLocation;
  let fixture: ComponentFixture<SearchLocation>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SearchLocation]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SearchLocation);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
