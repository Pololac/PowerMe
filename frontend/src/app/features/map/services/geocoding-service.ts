import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { map } from 'rxjs';
import { GeocodingResult } from '../search-location/geocoding-result.model';
import { runtimeEnv } from '../../../core/config/runtime-env';

@Injectable({
  providedIn: 'root',
})
export class GeocodingService {
  private readonly baseUrl = 'https://api.maptiler.com/geocoding';

  private http = inject(HttpClient);
  private readonly apiKey = runtimeEnv.MAPTILER_KEY;

  constructor() {
    if (!this.apiKey) {
      throw new Error('MAPTILER_KEY manquante (GeocodingService)');
    }
  }

  autocomplete(query: string) {
    return this.http
      .get<any>(`${this.baseUrl}/${query}.json`, {
        params: {
          key: this.apiKey,
          limit: 6,
          language: 'fr',
          country: 'fr',
          autocomplete: true,
        },
      })
      .pipe(
        map((res) => {
          const features = res.features ?? [];

          const results: GeocodingResult[] = features.map((f: any) => {
            return {
              id: f.id,
              label: f.text,
              secondaryLabel: f.place_name,
              lat: f.geometry.coordinates[1],
              lng: f.geometry.coordinates[0],
            };
          });

          return results;
        }),
      );
  }
}
