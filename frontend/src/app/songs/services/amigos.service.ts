import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Amigo {
  username: string;
  nombre: string;
  password: string;
  rol: string;
}

@Injectable({ providedIn: 'root' })
export class AmigosService {

  private http = inject(HttpClient);
  private baseUrl = environment.baseUrl;

  getAmigos(username: string): Observable<Amigo[]> {
    return this.http.get<Amigo[]>(`${this.baseUrl}/usuario/${username}/amigos`);
  }

}
