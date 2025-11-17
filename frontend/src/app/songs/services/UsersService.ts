import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

// Interface User ajustada al backend
export interface User {
  username: string;
  name: string;           // mapeado de 'nombre' del backend
  role: 'user' | 'admin'; // mapeado de 'rol' del backend
  isFollowing?: boolean;  // opcional para front
}

@Injectable({ providedIn: 'root' })
export class UsersService {

  private baseUrl = 'http://localhost:8080/api/usuario';

  suggestions = signal<User[]>([]);
  loading = signal<boolean>(false);

  constructor(private http: HttpClient) {}

  // Cargar sugerencias de amigos
  loadSuggestions(username: string) {
    this.loading.set(true);

    this.http.get<any[]>(`${this.baseUrl}/${username}/sugerencias`)
      .pipe(tap(() => this.loading.set(false)))
      .subscribe(
        data => {
          // Mapear campos del backend a la interface User
          const mapped: User[] = data.map(u => ({
            username: u.username,
            name: u.nombre,
            role: u.rol.toLowerCase() === 'admin' ? 'admin' : 'user',
            isFollowing: false // inicializar por defecto
          }));
          this.suggestions.set(mapped);
        },
        err => {
          console.error('Error cargando sugerencias:', err);
          this.suggestions.set([]);
          this.loading.set(false);
        }
      );
  }

  // Seguir a un usuario
  followUser(username: string, toFollow: string): Observable<boolean> {
    return this.http.post(`${this.baseUrl}/${username}/seguir/${toFollow}`, {})
      .pipe(map(() => true));
  }

  // Dejar de seguir a un usuario
  unfollowUser(username: string, toUnfollow: string): Observable<boolean> {
    return this.http.post(`${this.baseUrl}/${username}/dejarDeSeguir/${toUnfollow}`, {})
      .pipe(map(() => true));
  }
}
