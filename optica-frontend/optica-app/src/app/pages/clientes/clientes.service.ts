import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cliente } from './cliente.model';

@Injectable({ providedIn: 'root' })
export class ClientesService {
  private http = inject(HttpClient);
  // ajusta si tu API lleva prefijo (/optica)
  private baseUrl = 'http://localhost:8080/optica/clientes';

  list(): Observable<Cliente[]> {
    return this.http.get<Cliente[]>(this.baseUrl);
  }

  get(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.baseUrl}/${id}`);
  }

  /** Crea o actualiza según venga o no el id */
  save(c: Cliente): Observable<Cliente> {
    return this.http.post<Cliente>(this.baseUrl, c);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
