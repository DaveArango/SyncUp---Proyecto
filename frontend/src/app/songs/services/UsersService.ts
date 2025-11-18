import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';

// Interface adaptada al backend
export interface User {
  username: string;
  name: string;
  role: 'user' | 'admin';
  isFollowing?: boolean;
}

@Injectable({ providedIn: 'root' })
export class UsersService {

  private baseUrl = 'http://localhost:8080/api';

  suggestions = signal<User[]>([]);
  loading = signal<boolean>(false);

  constructor(private http: HttpClient) {}

  /** Cargar sugerencias usando el endpoint real */
  loadSuggestions(username: string, amigos: string[]) {
    this.loading.set(true);

    this.http
      .get<any[]>(`${this.baseUrl}/grafo-social/amigos-de-amigos/${username}`, {
        params: { niveles: 2 }
      })
      .subscribe({
        next: (data: any[]) => {

          const mapped: User[] = data.map((u: any): User => {
            const role: 'admin' | 'user' =
              (u?.rol?.toLowerCase() === 'administrador') ? 'admin' : 'user';

            return {
              username: String(u.username ?? ''),
              name: String(u.nombre ?? ''),
              role,
              isFollowing: amigos.includes(String(u.username ?? ''))
            };
          });

          this.suggestions.set(mapped);
          this.loading.set(false);
        },
        error: err => {
          console.error("Error:", err);
          this.suggestions.set([]);
          this.loading.set(false);
        }
      });
  }



  // Seguir
  followUser(username: string, toFollow: string): Observable<boolean> {

    return this.http
      .post(`${this.baseUrl}/usuario/${username}/seguir/${toFollow}`, {})
      .pipe(map(() => true));
  }

  // Dejar de seguir
  unfollowUser(username: string, toUnfollow: string): Observable<boolean> {
    return this.http
      .post(`${this.baseUrl}/usuario/${username}/dejarDeSeguir/${toUnfollow}`, {})
      .pipe(map(() => true));
  }
}
