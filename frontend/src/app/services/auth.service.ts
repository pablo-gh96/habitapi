import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Credentials {
  name: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  
  private baseUrl = 'http://localhost:8080/auth';

  register(body: Credentials): Observable<any> {
    return this.http.post(`${this.baseUrl}/register`, body);
  }

  login(body: Credentials): Observable<any> {
    return this.http.post(`${this.baseUrl}/login`, body);
  }
}
