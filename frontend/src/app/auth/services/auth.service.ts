import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { catchError, map, Observable, of, tap } from 'rxjs';
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
  login(email: string, password: string): Observable<boolean> {
    return this.http
      .post<AuthResponse>(`${baseUrl}/usuario/login`, { email, password })
      .pipe(
        map((resp) => this.handleAuthSuccess(resp)),
        catchError((error) => this.handleAuthError(error))
      );
  }

  // ---------------- REGISTER ----------------
  register(name: string, email: string, password: string): Observable<boolean> {
    return this.http
      .post<AuthResponse>(`${baseUrl}/auth/register`, { name, email, password })
      .pipe(
        map((resp) => this.handleAuthSuccess(resp)),
        catchError((error) => this.handleAuthError(error))
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
    const token = this._token();
    if (!token) return of(null);

    return this.http
      .get<User>(`${baseUrl}/usuario/perfil`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .pipe(
        tap((user) => this._user.set(user)),
        catchError(() => of(null))
      );
  }

  // ---------------- UPDATE PROFILE ----------------
  updateProfile(data: { name?: string; password?: string }): Observable<User | null> {
    const token = this._token();
    if (!token) return of(null);

    return this.http
      .put<User>(`${baseUrl}/users/update`, data, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .pipe(
        tap((user) => this._user.set(user)), // actualizar el usuario local
        catchError(() => of(null))
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

