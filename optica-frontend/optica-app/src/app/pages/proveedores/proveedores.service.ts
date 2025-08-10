// src/app/proveedores/proveedores.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Proveedor } from './proveedor.model';

@Injectable({ providedIn: 'root' })
export class ProveedoresService {
  private http = inject(HttpClient);

  // ⬅️ Ajusta este baseUrl al de tu API real (con o sin /optica)
  private baseUrl = 'http://localhost:8080/optica/proveedores';

  list(): Observable<Proveedor[]> {
    return this.http.get<Proveedor[]>(this.baseUrl);
  }

  get(id: number): Observable<Proveedor> {
    return this.http.get<Proveedor>(`${this.baseUrl}/${id}`);
  }

  /** Crea o actualiza según venga o no el id */
  save(p: Proveedor): Observable<Proveedor> {
    return this.http.post<Proveedor>(this.baseUrl, p);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
