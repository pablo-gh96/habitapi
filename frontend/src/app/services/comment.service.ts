import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Comment } from '../models/comment';
import { CreateComment } from '../dto/CreateComment';
import { CommentResponse } from '../dto/commentResponse';

@Injectable({ providedIn: 'root' })
export class CommentService {
  private readonly baseUrl = 'http://localhost:8080/api/comments';

  constructor(private http: HttpClient) {}

  /** Comentarios de un día donde participa userId (emisor o receptor) */
  getForDay(userId: number, dayISO: string): Observable<CommentResponse[]> {
    const params = new HttpParams().set('userId', userId).set('day', dayISO);
    return this.http.get<CommentResponse[]>(`${this.baseUrl}/day`, { params });
  }

    // ⬇️ nuevo: crear comentario
  create(body: CreateComment): Observable<Comment> {
    return this.http.post<Comment>(this.baseUrl, body);
  }
}
