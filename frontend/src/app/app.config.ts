// src/app/app.config.ts
import { ApplicationConfig, importProvidersFrom, provideZoneChangeDetection } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';

import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimations } from '@angular/platform-browser/animations';


import { ApiEnvelopeInterceptor } from './core/interceptors/api-envelope.interceptor';
import { HTTP_INTERCEPTORS } from '@angular/common/http';


// Angular Material (global, opcional)
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule }  from '@angular/material/button';
import { MatIconModule }    from '@angular/material/icon';
import { API_BASE_URL } from './core/api.tokens';
import { MatCardModule }    from '@angular/material/card';
import { environment } from '../environments/environment';
import { MatGridListModule } from '@angular/material/grid-list';

// Tu interceptor
import { authInterceptor } from './core/auth.interceptor';

export const appConfig: ApplicationConfig = {
  providers: [
    provideAnimations(),
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),

    // 👇 HttpClient + interceptor
    provideHttpClient(withInterceptors([authInterceptor])),
    { provide: API_BASE_URL, useValue: environment.apiBase },
    { provide: HTTP_INTERCEPTORS, useClass: ApiEnvelopeInterceptor, multi: true },

    // 👇 (opcional) módulos Material globales
    importProvidersFrom(
      BrowserAnimationsModule,
      MatToolbarModule,
      MatButtonModule,
      MatIconModule,
      MatCardModule,
      MatGridListModule
    ),
  ]
};
