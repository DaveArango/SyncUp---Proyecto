import {Injectable, inject, signal} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Observable, catchError, of } from 'rxjs';
import {map} from 'rxjs/operators';

export interface Song {
  id: number;
  titulo: string;
  artista: string;
  genero: string;
  anio: number;
  duracion: number;
  audio: string | null;
  rutaArchivo: string;
}

@Injectable({ providedIn: 'root' })
export class FavoritesService {
  private http = inject(HttpClient);
  private baseUrl = environment.baseUrl;

  getFavorites(username: string): Observable<Song[]> {
    return this.http
      .get<Song[]>(`${this.baseUrl}/usuario/${username}/favoritas`)
      .pipe(
        catchError(() => of([]))
      );
  }

  addFavorite(username: string, songId: number): Observable<boolean> {
    return this.http
      .post<boolean>(`${this.baseUrl}/usuario/${username}/favoritos`, { songId })
      .pipe(
        catchError(() => of(false))
      );
  }

  removeFavorite(username: string, songId: number): Observable<boolean> {
    return this.http
      .delete<boolean>(`${this.baseUrl}/usuario/${username}/favoritos/eliminar`, {
        body: { id: songId }   // 🔥 AHORA SÍ usa "id"
      })
      .pipe(
        map(() => true),
        catchError(() => of(false))
      );
  }
  favoritesIds = signal<number[]>([]);

  // Llamar al backend y actualizar señal
  loadFavorites(username: string) {
    this.getFavorites(username).subscribe(songs => {
      this.favoritesIds.set(songs.map(s => s.id));
    });
  }

}
