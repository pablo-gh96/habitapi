import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserSummary {
  id: number;
  username: string;
  name: string;
  lastname: string;
}

@Injectable({ providedIn: 'root' })
export class UsersService {
  private readonly baseUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  /** Lista de usuarios excepto el mío */
  getOthers(): Observable<UserSummary[]> {
    return this.http.get<UserSummary[]>(`${this.baseUrl}/ids-others`);
  }

  getNameById(id: number): Observable<{name: string}> {
    return this.http.get<{name: string}>(`${this.baseUrl}/${id}/name`);
  }

  getIdByUsername(username: string): Observable<{id: number}> {
    return this.http.get<{id: number}>(`${this.baseUrl}/username/${username}/id`);
  }  
}
