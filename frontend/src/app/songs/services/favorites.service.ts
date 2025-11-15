import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, catchError, map, of } from 'rxjs';

export interface Song {
  id: string;
  title: string;
  url?: string;
  artist?: string;
  album?: string;
  year?: number;
}

@Injectable({ providedIn: 'root' })
export class FavoritesService {
  private http = inject(HttpClient);
  private baseUrl = environment.baseUrl;

  getFavorites(userId: string): Observable<Song[]> {
    return this.http.get<Song[]>(`${this.baseUrl}/favorites/${userId}`).pipe(
      catchError(() => of([])) // Si hay error, retorna lista vacía
    );
  }

  addFavorite(userId: string, songId: string): Observable<boolean> {
    return this.http.post(`${this.baseUrl}/favorites/${userId}`, { songId }).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }

  removeFavorite(userId: string, songId: string): Observable<boolean> {
    return this.http.delete(`${this.baseUrl}/favorites/${userId}/${songId}`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }
}
