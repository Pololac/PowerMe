import { ChangeDetectionStrategy, Component, effect, inject, signal } from '@angular/core';
import { MapService } from '../services/map-service';
import { GeocodingService } from '../services/geocoding-service';
import { CommonModule } from '@angular/common';
import { GeocodingResult } from './geocoding-result.model';

@Component({
  selector: 'app-search-location',
  imports: [CommonModule],
  templateUrl: './search-location.html',
  styleUrl: './search-location.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SearchLocation {
  private mapService = inject(MapService);
  private geocoding = inject(GeocodingService);

  readonly query = signal('');
  readonly results = signal<GeocodingResult[]>([]);
  readonly hasSelection = signal(false);

  readonly showFilters = signal(false);
  readonly showInfo = signal(false);

  private lastQuery = '';

  constructor() {
    effect(() => {
      const q = this.query().trim();

      if (this.hasSelection()) return;

      if (q.length < 3 || q === this.lastQuery) return;

      this.lastQuery = q;
      this.search(q);
    });
  }

  private search(q: string) {
    this.geocoding.autocomplete(q).subscribe((results) => {
      this.results.set(results);
    });
  }

  onInput(event: Event) {
    this.hasSelection.set(false);
    this.query.set((event.target as HTMLInputElement).value);
  }

  select(result: GeocodingResult) {
    this.hasSelection.set(true);
    this.mapService.setSearchPosition([result.lng, result.lat]);
    this.query.set(result.secondaryLabel);
    this.results.set([]);
  }

  openFilters() {
    this.showFilters.set(true);
  }

  openInfo() {
    this.showInfo.set(true);
  }

  closeModals() {
    this.showFilters.set(false);
    this.showInfo.set(false);
  }

  clear() {
    this.query.set('');
    this.results.set([]);
    this.mapService.resetSearch();
  }
}
