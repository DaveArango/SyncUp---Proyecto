import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Cancion {
  id: number;
  titulo: string;
  artista: string;
  genero: string;
  anio: number;
  duracion: number;
  audio: string | null;
  rutaArchivo: string;
}

@Injectable({
  providedIn: 'root'
})
export class GrafoService {

  private apiUrl = 'http://localhost:8080/api/grafo';

  constructor(private http: HttpClient) {}

  obtenerSimilares(id: number): Observable<Cancion[]> {
    return this.http.get<Cancion[]>(`${this.apiUrl}/similares/${id}`);
  }
}
