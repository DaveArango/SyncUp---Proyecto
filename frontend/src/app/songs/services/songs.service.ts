import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../environments/environment.development';
import { GiphyResponse } from '../interfaces/giphy.interfaces';
import { Song } from '../interfaces/song.interface';
import { SongMapper } from '../mapper/song.mapper';
import { signal, computed, effect } from '@angular/core';

const SONG_KEY = 'songs';

@Injectable({ providedIn: 'root' })
export class SongService {

  private http = inject(HttpClient);

  allSongs = signal<Song[]>([]);
  trendingSongsLoading = signal(true);

  searchHistory = signal<Record<string, Song[]>>(this.loadFromLocalStorage());
  searchHistoryKeys = computed(() => Object.keys(this.searchHistory()));

  constructor() {
    this.fetchAllSongs();

    effect(() => {
      const historyString = JSON.stringify(this.searchHistory());
      localStorage.setItem(SONG_KEY, historyString);
    });
  }

  private loadFromLocalStorage() {
    const data = localStorage.getItem(SONG_KEY) ?? '{}';
    return JSON.parse(data);
  }

  fetchAllSongs() {
    this.http.get<any[]>(`${environment.baseUrl}/usuario/canciones/listar`)
      .subscribe({
        next: (resp) => this.allSongs.set(SongMapper.mapArray(resp)),
        error: (err) => console.error('Error cargando canciones:', err)
      });
  }

  getWeeklyDiscoveryFromAPI() {
    return this.http.get<Song[]>(`${environment.baseUrl}/discovery/weekly`);
  }

  getSongAudioUrl(id: number) {
    return `${environment.baseUrl}/usuario/canciones/reproducir/${id}`;
  }

  getHistorySongs(query: string): Song[] {
    return this.searchHistory()[query] ?? [];
  }

  searchSuggestions(prefijo: string) {
    return this.http.get<string[]>(`${environment.baseUrl}/usuario/canciones/autocompletar`, {
      params: { prefijo }
    });
  }

  searchSongsByName(nombre: string) {
    return this.http.get<Song[]>(`${environment.baseUrl}/usuario/canciones/buscar-por-nombre`, {
      params: { nombre }
    });
  }

  getFavorites(userId: string) {
    return this.http.get<Song[]>(`${environment.baseUrl}/usuario/canciones/listar-favoritos/${userId}`);
  }

  removeFavorite(userId: string, songId: number) {
    return this.http.delete(`${environment.baseUrl}/usuario/canciones/favoritos/${userId}/${songId}`);
  }

  getSimilarSongs(id: number) {
  return this.http.get<Song[]>(`${environment.baseUrl}/grafo/similares/${id}`)
}

getRadioSongs(id: number) {
  return this.http.get<Song[]>(`${environment.baseUrl}/usuario/canciones/radio/${id}`);
}

  addFavorite(username: string, songId: number) {
    return this.http.post(`${environment.baseUrl}/usuario/${username}/favoritos`, {
      id: songId
    });
  }

  advancedSearch(artista: string, genero: string, anio: number | null, esAnd: boolean) {
    let params: any = {};

    if (artista) params.artista = artista;
    if (genero) params.genero = genero;
    if (anio) params.anio = anio;
    params.esAnd = esAnd;

    return this.http.get<Song[]>(`${environment.baseUrl}/usuario/canciones/busqueda-avanzada`, {
      params
    });
  }


  getSongDescubrimientoSemanal(username: string) {
    debugger;
    return this.http.get<Song[]>(
      `${environment.baseUrl}/usuario/canciones/descubrimiento/${username}`
    );
  }
//http://localhost:8080/api/usuario/canciones/radio/5
  getSongsByRadio(songId: number) {
    return this.http.get<Song[]>(`${environment.baseUrl}/usuario/canciones/radio/${songId}`);
  }

  getAllSongs() {
    return this.http.get<Song[]>(`${environment.baseUrl}/admin/canciones/listar`);
  }
}
