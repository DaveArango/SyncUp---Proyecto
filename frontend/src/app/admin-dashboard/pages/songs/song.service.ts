import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Song } from '../../../songs/interfaces/song.interface';
import { environment } from '../../../../environments/environment';


@Injectable({ providedIn: 'root' })
export class SongsService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.baseUrl}/songs`;

  getAll(): Observable<Song[]> {
    return this.http.get<Song[]>(this.baseUrl).pipe(catchError(() => of([])));
  }

  getById(id: string): Observable<Song | null> {
    return this.http.get<Song>(`${this.baseUrl}/${id}`).pipe(catchError(() => of(null)));
  }

  create(song: Partial<Song>): Observable<Song | null> {
    return this.http.post<Song>(this.baseUrl, song).pipe(catchError(() => of(null)));
  }

  update(id: string, song: Partial<Song>): Observable<Song | null> {
    return this.http.put<Song>(`${this.baseUrl}/${id}`, song).pipe(catchError(() => of(null)));
  }

  delete(id: string): Observable<boolean> {
    return this.http.delete(`${this.baseUrl}/${id}`).pipe(
      catchError(() => of(false)),
      // Map response a boolean si tu backend no devuelve nada
      map(() => true)
    );
  }
}
