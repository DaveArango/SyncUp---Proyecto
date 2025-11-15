import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, catchError, of, map } from 'rxjs';

export interface SongAdmin {
  id: string;
  title: string;
  artist: string;
  album: string;
  year: number;
  url?: string;
}

@Injectable({ providedIn: 'root' })
export class AdminSongService {
  private http = inject(HttpClient);
  private baseUrl = environment.baseUrl;

  getSongs(): Observable<SongAdmin[]> {
    return this.http.get<SongAdmin[]>(`${this.baseUrl}/admin/songs`).pipe(
      catchError(() => of([]))
    );
  }

  addSong(song: Omit<SongAdmin, 'id'>): Observable<SongAdmin | null> {
    return this.http.post<SongAdmin>(`${this.baseUrl}/admin/songs`, song).pipe(
      catchError(() => of(null))
    );
  }

  updateSong(songId: string, song: Partial<SongAdmin>): Observable<SongAdmin | null> {
    return this.http.put<SongAdmin>(`${this.baseUrl}/admin/songs/${songId}`, song).pipe(
      catchError(() => of(null))
    );
  }

  deleteSong(songId: string): Observable<boolean> {
    return this.http.delete(`${this.baseUrl}/admin/songs/${songId}`).pipe(
      map(() => true),
      catchError(() => of(false))
    );
  }
}
