// src/app/pages/clientes/clientes.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente, ClienteCreate, ClientePatch } from './cliente.model';

@Injectable({ providedIn: 'root' })
export class ClientesService {
  private http = inject(HttpClient);
  // Ajusta a tu API real (puede venir de environment.apiBase)
  private baseUrl = 'http://localhost:8080/omniventas/api/clientes';

  list(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.baseUrl);
  }

  get(uid: string): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.baseUrl}/${uid}`);
  }

  create(body: ClienteCreate): Observable<Cliente> {
    return this.http.post<Cliente>(this.baseUrl, body);
  }

  update(uid: string, patch: ClientePatch): Observable<Cliente> {
    return this.http.patch<Cliente>(`${this.baseUrl}/${uid}`, patch);
  }

  delete(uid: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${uid}`);
  }
}
