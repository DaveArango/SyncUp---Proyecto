import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { environment } from './environment';

export interface UserSuggestion {
  id: string;
  name: string;
  username: string;
  isFollowing: boolean; // indica si ya lo sigues
}

@Injectable({ providedIn: 'root' })
export class UsersService {
  private http = inject(HttpClient);
  private baseUrl = environment.baseUrl;

  // Lista de sugerencias
  suggestions = signal<UserSuggestion[]>([]);
  loading = signal(false);

  // Traer sugerencias del backend
  loadSuggestions(userId: string) {
    this.loading.set(true);
    this.http.get<UserSuggestion[]>(`${this.baseUrl}/users/suggestions/${userId}`)
      .pipe(catchError(() => of([])))
      .subscribe(users => {
        this.suggestions.set(users);
        this.loading.set(false);
      });
  }

  // Seguir a un usuario
  followUser(userId: string, targetId: string): Observable<boolean> {
    return this.http.post(`${this.baseUrl}/users/${userId}/follow`, { targetId })
      .pipe(
        catchError(() => of(false)),
        map(() => true)
      );
  }

  // Dejar de seguir a un usuario
  unfollowUser(userId: string, targetId: string): Observable<boolean> {
    return this.http.post(`${this.baseUrl}/users/${userId}/unfollow`, { targetId })
      .pipe(
        catchError(() => of(false)),
        map(() => true)
      );
  }
}
