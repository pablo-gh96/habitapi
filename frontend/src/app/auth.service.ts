import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private api = 'http://localhost:8080';
  constructor(private http: HttpClient) {}

  login(username: string, password: string) {
    return this.http.post<{token:string, username:string}>(`${this.api}/auth/login`, { username, password })
      .pipe(tap(res => localStorage.setItem('token', res.token)));
  }

  me() { return this.http.get<{username:string}>(`${this.api}/auth/me`); }

  logout() { localStorage.removeItem('token'); }
}
