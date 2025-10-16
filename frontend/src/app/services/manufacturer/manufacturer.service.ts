import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '@environments/environment';
import { Manufacturer, ManufacturerFilter } from '@models/manufacturer';

@Injectable({ providedIn: 'root' })
export class ManufacturerService {
  private readonly http = inject(HttpClient);
  private readonly resourceUrl = `${environment.apiUrl}/fabricantes`;

  list(filter?: ManufacturerFilter): Observable<Manufacturer[]> {
    let params = new HttpParams();
    if (filter) {
      Object.entries(filter).forEach(([key, value]) => {
        if (value) {
          params = params.set(key, value);
        }
      });
    }
    return this.http.get<Manufacturer[]>(this.resourceUrl, { params });
  }

  findById(id: number): Observable<Manufacturer> {
    return this.http.get<Manufacturer>(`${this.resourceUrl}/${id}`);
  }

  create(payload: Partial<Manufacturer>): Observable<Manufacturer> {
    return this.http.post<Manufacturer>(this.resourceUrl, payload);
  }

  update(id: number, payload: Partial<Manufacturer>): Observable<Manufacturer> {
    return this.http.put<Manufacturer>(`${this.resourceUrl}/${id}`, payload);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.resourceUrl}/${id}`);
  }
}
