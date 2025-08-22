import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private key = 'token';
  isLoggedInSig = signal<boolean>(!!localStorage.getItem(this.key));

  get token(): string | null { return localStorage.getItem(this.key); }

  login(token: string) {
    localStorage.setItem(this.key, token);
    this.isLoggedInSig.set(true);
  }

  logout() {
    localStorage.removeItem(this.key);
    this.isLoggedInSig.set(false);
  }
}
