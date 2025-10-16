import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '@environments/environment';
import { Vehicle, VehicleFilter, VehiclePayload } from '@models/vehicle';

@Injectable({ providedIn: 'root' })
export class VehicleService {
  private readonly http = inject(HttpClient);
  private readonly resourceUrl = `${environment.apiUrl}/veiculos`;

  list(filter?: VehicleFilter): Observable<Vehicle[]> {
    let params = new HttpParams();
    if (filter) {
      Object.entries(filter).forEach(([key, value]) => {
        if (value !== undefined && value !== null && value !== '') {
          params = params.set(key, value as string);
        }
      });
    }
    return this.http.get<Vehicle[]>(this.resourceUrl, { params });
  }

  findById(id: number): Observable<Vehicle> {
    return this.http.get<Vehicle>(`${this.resourceUrl}/${id}`);
  }

  create(payload: VehiclePayload): Observable<Vehicle> {
    return this.http.post<Vehicle>(this.resourceUrl, payload);
  }

  update(id: number, payload: VehiclePayload): Observable<Vehicle> {
    return this.http.put<Vehicle>(`${this.resourceUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.resourceUrl}/${id}`);
  }
}
