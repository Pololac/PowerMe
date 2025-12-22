import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FavoriteStations } from './favorite-stations';

describe('FavoriteStations', () => {
  let component: FavoriteStations;
  let fixture: ComponentFixture<FavoriteStations>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FavoriteStations]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FavoriteStations);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
