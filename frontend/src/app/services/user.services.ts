import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserSummary {
  id: number;
  name: string;
}

@Injectable({ providedIn: 'root' })
export class UsersService {
  private readonly baseUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  /** Lista de usuarios excepto el mío */
  getOthers(myId: number, page?: number, size?: number): Observable<UserSummary[]> {
    let params = new HttpParams().set('myId', myId);
    if (page != null && size != null) {
      params = params.set('page', page).set('size', size);
    }
    return this.http.get<UserSummary[]>(`${this.baseUrl}/others`, { params });
  }
}
