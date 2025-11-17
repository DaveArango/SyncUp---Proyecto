import {HttpClient, HttpParams} from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import {catchError, map, Observable, of, tap, throwError} from 'rxjs';
import { User } from '../interfaces/user.interface';
import { AuthResponse } from '../interfaces/auth-response.interface';
import { environment } from '../../../environments/environment';

type AuthStatus = 'checking' | 'authenticated' | 'not-authenticated';
const baseUrl = environment.baseUrl;

@Injectable({ providedIn: 'root' })
export class AuthService {
  private _authStatus = signal<AuthStatus>('checking');
  private _user = signal<User | null>(null);
  private _token = signal<string | null>(localStorage.getItem('token'));

  private http = inject(HttpClient);

  authStatus = computed<AuthStatus>(() => {
    if (this._authStatus() === 'checking') return 'checking';
    if (this._user()) return 'authenticated';
    return 'not-authenticated';
  });

  user = computed(() => this._user());
  token = computed(this._token);

  // ---------------- LOGIN ----------------
  login(username: string, password: string) {
    const params = new HttpParams()
      .set('username', username)
      .set('password', password);

    return this.http.post<any>(`${baseUrl}/usuario/login`, null, { params })
      .pipe(
        tap(resp => {
          if (resp?.usuario) {
            // Guardamos el usuario correctamente
            this._user.set({
              id: '',          // opcional
              name: resp.usuario.nombre,
              username: resp.usuario.username,       // opcional
              isActive: true,  // opcional
              role: 'user'     // opcional
            });
          }
        }),
        map(() => true),
        catchError(err => throwError(() => err))
      );
  }

  // ---------------- REGISTER ----------------
  register(username: string, nombre: string, password: string): Observable<boolean> {
    const body = {
      username,  // va el username
      nombre,    // va el nombre real
      password
    };

    return this.http.post(`${baseUrl}/usuario/registrar`, body)
      .pipe(
        map(() => true),
        catchError((error) => {
          console.error('Error registrando usuario:', error);
          return of(false);
        })
      );
  }

  // ---------------- CHECK TOKEN ----------------
  checkStatus(): Observable<boolean> {
    const token = localStorage.getItem('token');
    if (!token) {
      this.logout();
      return of(false);
    }

    return this.http
      .get<AuthResponse>(`${baseUrl}/auth/check-status`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .pipe(
        map((resp) => this.handleAuthSuccess(resp)),
        catchError((error) => this.handleAuthError(error))
      );
  }
  // ---------------- GET PROFILE ----------------
  getProfile(): Observable<User | null> {
    const username = this._user()?.username; // aquí usamos username
    if (!username) return of(null);

    return this.http.get<User>(`${baseUrl}/usuario/perfil/${username}`)
      .pipe(
        tap(user => this._user.set(user)),
        catchError(() => of(null))
      );
  }
  // ---------------- UPDATE PROFILE ----------------
  updateProfile(data: { name?: string; password?: string }): Observable<User | null> {
    const user = this._user();
    if (!user || !user.username) return of(null);

    let params = new HttpParams();
    if (data.name) params = params.set('nombre', data.name);
    if (data.password) params = params.set('password', data.password);

    return this.http.put<User>(`${baseUrl}/usuario/perfil/${user.username}`, null, { params })
      .pipe(
        tap(u => this._user.set(u)),
        catchError(err => {
          console.error('Error actualizando perfil:', err);
          return of(null);
        })
      );
  }
  // ---------------- LOGOUT ----------------
  logout() {
    this._user.set(null);
    this._token.set(null);
    this._authStatus.set('not-authenticated');
    localStorage.removeItem('token');
  }

  // ---------------- HANDLERS ----------------
  private handleAuthSuccess({ token, user }: AuthResponse) {
    this._user.set(user);
    this._authStatus.set('authenticated');
    this._token.set(token);
    localStorage.setItem('token', token);
    return true;
  }

  private handleAuthError(error: any) {
    console.error('Auth error:', error);
    this.logout();
    return of(false);
  }
  // Método para saber si es admin
  isAdmin(): boolean {
    const user = this._user();
    return !!user && user.role === 'admin'; // o como definas el rol en tu User interface
  }
}
