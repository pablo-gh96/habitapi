import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface UserSummary {
  id: number;
  username: string;
  name: string;
  lastname: string;
}

@Injectable({ providedIn: 'root' })
export class UsersService {
  private readonly baseUrl = environment.API_BASE_URL+'/api/users';

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
  
  getUserByUsername(username: string): Observable<UserSummary> {
    return this.http.get<UserSummary>(`${this.baseUrl}/username/${username}`);
  }
}
