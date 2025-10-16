import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface Credentials {
  username: string;
  password: string;
  name: string;
  lastName: string;
  email: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  
  private baseUrl = 'http://localhost:8080';

  register(body: Credentials): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, body);
  }

  login(username: string, password: string): Observable<any> {
    return this.http.post<{ token: string, username: string }>(this.baseUrl + '/login', { username, password })
      .pipe(
        tap(response => {
          // Guardar en sessionStorage
          sessionStorage.setItem('token', response.token);
          sessionStorage.setItem('username', response.username);
        })
      );
  }

  logout() {
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('username');
  }

  getToken(): string | null {
    return sessionStorage.getItem('token');
  }

  getUsername(): string | null {
    return sessionStorage.getItem('username');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }
}
