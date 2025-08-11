import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpResponse,
  HttpErrorResponse,
} from '@angular/common/http';
import { Observable, map, catchError, throwError } from 'rxjs';

interface ApiEnvelope<T = any> {
  success: boolean;
  data: T;
  error?: { message?: string; [k: string]: any };
}

function isEnvelope(body: unknown): body is ApiEnvelope {
  return !!body && typeof body === 'object' && 'success' in (body as any);
}

@Injectable()
export class ApiEnvelopeInterceptor implements HttpInterceptor {
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(req).pipe(
      // Desenvuelve respuestas exitosas { success:true, data }
      map(ev => {
        if (ev instanceof HttpResponse && isEnvelope(ev.body)) {
          const env = ev.body as ApiEnvelope;
          if (env.success) {
            return ev.clone({ body: env.data });
          }
        }
        return ev;
      }),

      // Normaliza errores: deja en .error el objeto "error" del envelope
      catchError((err: HttpErrorResponse) => {
        const env = isEnvelope(err?.error) ? (err.error as ApiEnvelope) : null;

        const normalizedError = env?.error ?? err.error;
        const normalizedStatus =
          (env?.error && typeof (env.error as any).status === 'number'
            ? (env.error as any).status
            : err.status) as number;

        return throwError(
          () =>
            new HttpErrorResponse({
              error: normalizedError,
              headers: err.headers,
              status: normalizedStatus,
              statusText: err.statusText,
              url: err.url ?? undefined, // null -> undefined
            })
        );
      })
    );
  }
}
