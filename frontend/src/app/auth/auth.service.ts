import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';

type LoginReq = { email: string; password: string };
type RegisterReq = { email: string; fullName: string; password: string };
type AuthRes = { accessToken: string; expiresInSeconds: number };
export type MeRes = { id: string; email: string; fullName: string | null };

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly tokenKey = 'accessToken';
  constructor(private http: HttpClient, private router: Router) {}

  login(payload: LoginReq) {
    return this.http.post<AuthRes>('/api/auth/login', payload).pipe(
      tap(res => {
        localStorage.setItem(this.tokenKey, res.accessToken);
        this.router.navigateByUrl('/dashboard');
      })
    );
  }

  register(payload: RegisterReq) {
    return this.http.post<void>('/api/auth/register', payload);
  }

  me() { return this.http.get<MeRes>('/api/auth/me'); }
  logout() { localStorage.removeItem(this.tokenKey); this.router.navigateByUrl('/login'); }
  get token() { return localStorage.getItem(this.tokenKey); }
  isAuthenticated() { return !!this.token; }
}
