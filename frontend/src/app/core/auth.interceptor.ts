// auth.interceptor.ts
import { HttpInterceptorFn } from '@angular/common/http';

function isAuthUrl(url: string): boolean {
  // Ajusta si tu baseUrl cambia. Incluye /omniventas si aplica.
  return url.includes('/auth/login') || url.includes('/auth/refresh');
}

function isExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split('.')[1] || ''));
    const expMs = (payload?.exp ?? 0) * 1000;
    // skew 30s por desfaces de reloj
    return !expMs || expMs <= (Date.now() - 30_000);
  } catch {
    return true;
  }
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const token = localStorage.getItem('token');

  // 1) Nunca adjuntar Authorization a /auth/*
  if (isAuthUrl(req.url)) {
    return next(req);
  }

  // 2) Adjuntar solo si hay token y no está expirado
  let finalReq = req;
  if (token && !isExpired(token)) {
    finalReq = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  } else if (token) {
    // limpia token vencido
    localStorage.removeItem('token');
  }

  return next(finalReq);
};
