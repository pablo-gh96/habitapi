import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { WeeklyPlan, WeeklyPlanRequest } from '../models/weekly-plan';
import { environment } from '../../environments/environment';

@Injectable({
    providedIn: 'root'
})
export class WeeklyPlanService {
    private apiUrl = `${environment.API_BASE_URL}/api/weeks`;

    constructor(private http: HttpClient) { }

    getWeek(startDate: string): Observable<WeeklyPlan> {
        let params = new HttpParams().set('startDate', startDate);
        return this.http.get<WeeklyPlan>(this.apiUrl, { params });
    }

    updateWeek(id: number, request: WeeklyPlanRequest): Observable<WeeklyPlan> {
        return this.http.put<WeeklyPlan>(`${this.apiUrl}/${id}`, request);
    }
}
