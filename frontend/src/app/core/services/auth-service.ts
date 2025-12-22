import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { User } from '../models/domain/user.model';
import { map, Observable } from 'rxjs';
import { Router } from '@angular/router';
import { LoginRequest } from '../models/requests/login.request';
import { LoginResponseDto } from '../models/dto/login.response.dto';
import { RegisterRequest } from '../models/requests/register.request';
import { MessageResponseDto } from '../models/dto/message.response.dto';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  readonly user = signal<User | null>(null);
  readonly isLogged = computed(() => {
    console.log('isLogged recomputed', this.user());
    return this.user() !== null;
  });

  private storage: Storage = sessionStorage;

  constructor() {
    const storedUser = localStorage.getItem('user') ?? sessionStorage.getItem('user');

    if (!storedUser) {
      return;
    }

    try {
      this.user.set(JSON.parse(storedUser));
    } catch (e) {
      localStorage.removeItem('user');
      sessionStorage.removeItem('user');
      console.warn('Failed to restore user from localStorage', e);
    }
  }

  login(data: LoginRequest): Observable<{ token: string; user: User }> {
    return this.http.post<LoginResponseDto>('/auth/login', data).pipe(
      map((res) => ({
        token: res.accessToken,
        user: res.user,
      })),
    );
  }

  register(data: RegisterRequest): Observable<MessageResponseDto> {
    return this.http.post<MessageResponseDto>('/account/register', data);
  }

  forgotPassword(email: string) {
    return this.http.post<MessageResponseDto>(
      `/account/password/forgot/${encodeURIComponent(email)}`,
      null,
    );
  }

  resetPassword(token: string, newPassword: string) {
    return this.http.post<MessageResponseDto>('/account/password/reset', {
      token,
      newPassword,
    });
  }

  refreshToken() {
    return this.http
      .post<{ accessToken: string }>('/auth/refresh', {}, { withCredentials: true })
      .pipe(map((res) => res.accessToken));
  }

  logout() {
    this.clearSession();

    this.router.navigateByUrl('/');
  }

  setSession(token: string, user: User, rememberMe: boolean) {
    this.storage = rememberMe ? localStorage : sessionStorage;

    this.storage.setItem('token', token);
    this.storage.setItem('user', JSON.stringify(user));

    this.user.set(user);
  }

  clearSession() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    sessionStorage.removeItem('token');
    sessionStorage.removeItem('user');
    this.user.set(null);
  }
}
