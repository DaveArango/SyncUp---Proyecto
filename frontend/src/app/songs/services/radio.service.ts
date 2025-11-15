import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { Song } from './favorites.service';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class RadioService {
  private http = inject(HttpClient);
  private baseUrl = environment.baseUrl;

  // Cola de canciones de la radio
  radioQueue = signal<Song[]>([]);
  loading = signal(false);

  startRadioFromSong(songId: string): Observable<Song[]> {
    this.loading.set(true);
    return this.http.get<Song[]>(`${this.baseUrl}/radio/${songId}`);
  }

  loadRadio(songId: string) {
    this.startRadioFromSong(songId).subscribe({
      next: (songs) => {
        this.radioQueue.set(songs);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error cargando radio', err);
        this.loading.set(false);
      }
    });
  }
}
