import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import { environment } from '@environments/environment';
import { AuthCredentials, AuthResponse } from '@models/auth-credentials';

const TOKEN_STORAGE_KEY = 'integrador.token';
const USERNAME_STORAGE_KEY = 'integrador.username';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenSignal = signal<string | null>(localStorage.getItem(TOKEN_STORAGE_KEY));
  private readonly userSignal = signal<string | null>(localStorage.getItem(USERNAME_STORAGE_KEY));

  login(credentials: AuthCredentials): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${environment.apiUrl}/auth/login`, credentials).pipe(
      tap((response) => {
        localStorage.setItem(TOKEN_STORAGE_KEY, response.token);
        localStorage.setItem(USERNAME_STORAGE_KEY, response.name);
        this.tokenSignal.set(response.token);
        this.userSignal.set(response.name);
      })
    );
  }

  logout(): void {
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    localStorage.removeItem(USERNAME_STORAGE_KEY);
    this.tokenSignal.set(null);
    this.userSignal.set(null);
  }

  getToken(): string | null {
    return this.tokenSignal();
  }

  getUsername(): string | null {
    return this.userSignal();
  }

  isAuthenticated(): boolean {
    return !!this.getToken();
  }

  tokenChanges() {
    return this.tokenSignal.asReadonly();
  }
}
