import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface User {
  nombre: string;
  username: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class UsersService {
  private http = inject(HttpClient);

  allUsers = signal<User[]>([]);

  fetchAllUsers() {
    this.http.get<User[]>(`${environment.baseUrl}/usuario/todos`)
      .subscribe({
        next: users => this.allUsers.set(users),
        error: err => console.error('Error cargando usuarios:', err)
      });
  }

  deleteUser(username: string) {
    return this.http.delete(`${environment.baseUrl}/usuario/eliminar/${username}`);
  }
}
