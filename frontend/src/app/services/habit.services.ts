import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Habit } from '../models/habit';
import { CreateHabit } from '../dto/CreateHabit';

@Injectable({ providedIn: 'root' })
export class HabitService {
  private readonly baseUrl = 'http://localhost:8080/api/habits';

  constructor(private http: HttpClient) {}

  /** Devuelve todos los hábitos del mes indicado */
  getHabits(year: number, month: number): Observable<Habit[]> {
    const params = new HttpParams()
      .set('year', String(year))
      .set('month', String(month));

    return this.http.get<Habit[]>(this.baseUrl, { params });
  }

    create(habit: CreateHabit): Observable<Habit[]> {
    return this.http.post<Habit[]>(this.baseUrl, habit);
  }

    updateStatus(id: number): Observable<Habit> {
    return this.http.patch<Habit>(`${this.baseUrl}/${id}/status`, {});
  }

    delete(id: number) {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
    }

    deleteByTitle(title: string) {
    return this.http.delete<{ deleted: number }>(`${this.baseUrl}/by-title`, { params: { title } });
    }
}
