import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { ChargingLocationDetailDto } from '../../../../core/models/dto/charging-location-detail.dto';

@Injectable({
  providedIn: 'root',
})
export class ChargingLocationApi {
  private readonly http = inject(HttpClient);

  getById(id: number) {
    return this.http.get<ChargingLocationDetailDto>(`/charging-locations/${id}`);
  }
}
