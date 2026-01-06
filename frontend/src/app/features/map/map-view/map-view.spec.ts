import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MapView } from './map-view';

describe('MapView', () => {
  let component: MapView;
  let fixture: ComponentFixture<MapView>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MapView]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MapView);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
