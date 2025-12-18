import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Plate } from '../models/plate';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class PlateService {
    private apiUrl = `${environment.API_BASE_URL}/api/plates`;

    constructor(private http: HttpClient) { }

    getAllPlates(): Observable<Plate[]> {
        return this.http.get<Plate[]>(this.apiUrl);
    }

    createPlate(plate: { name: string; type: string }): Observable<Plate> {
        return this.http.post<Plate>(this.apiUrl, plate);
    }

    deletePlate(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
