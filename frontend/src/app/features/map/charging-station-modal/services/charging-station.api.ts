import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ChargingStationDetailDto } from '../../../../core/models/dto/charging-station-detail.dto';
import { ChargingStationRequest } from '../../../../core/models/requests/charging-station.request';
import { ChargingStationAvailabilityDto } from '../../../../core/models/dto/charging-station-availability.dto';

@Injectable({ providedIn: 'root' })
export class ChargingStationApi {
  private readonly http = inject(HttpClient);

  getOne(id: number) {
    return this.http.get<ChargingStationDetailDto>(`/charging-stations/${id}`);
  }

  getAvailability(
    stationId: number,
    date: string, // YYYY-MM-DD
  ) {
    return this.http.get<ChargingStationAvailabilityDto>(
      `/charging-stations/${stationId}/availability`,
      { params: { date } },
    );
  }

  create(locationId: number, payload: ChargingStationRequest) {
    return this.http.post<ChargingStationDetailDto>(
      `/charging-locations/${locationId}/stations`,
      payload,
    );
  }

  update(id: number, payload: ChargingStationRequest) {
    return this.http.put<ChargingStationDetailDto>(`/charging-stations/${id}`, payload);
  }

  delete(id: number) {
    return this.http.delete<void>(`/charging-stations/${id}`);
  }
}
