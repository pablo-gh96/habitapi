import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Habit } from '../models/habit';
import { CreateHabit } from '../dto/createHabit';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class HabitService {
  private readonly baseUrl = environment.API_BASE_URL+'/api/habits';

  constructor(private http: HttpClient) {}

  /** Habitos de un mes por usuario */
  getHabits(userId: number, year?: number, month?: number): Observable<Habit[]> {
    let params = new HttpParams().set('userId', String(userId));
    if (year != null)  params = params.set('year', String(year));
    if (month != null) params = params.set('month', String(month));
    return this.http.get<Habit[]>(this.baseUrl, { params });
  }

  
  create(habit: CreateHabit): Observable<Habit[]> {
    // habit.userId debe venir informado desde el componente
    return this.http.post<Habit[]>(this.baseUrl, habit);
  }

  /** Toggle de estado asegurando pertenencia a userId */
  updateStatus(userId: number, id: number): Observable<Habit> {
    
    return this.http.put<Habit>(`${this.baseUrl}/${id}/status`, {}, );
  }

  /** Borrado de una ocurrencia por id y userId */
  delete(userId: number, id: number): Observable<void> {
    const params = new HttpParams().set('userId', String(userId));
    return this.http.delete<void>(`${this.baseUrl}/${id}`, { params });
  }

  /** Borrado masivo por título para un usuario */
  deleteByTitle(userId: number, title: string): Observable<{ deleted: number }> {
    const params = new HttpParams()
      .set('title', title);
    return this.http.delete<{ deleted: number }>(`${this.baseUrl}/by-title`, { params });
  }
}
