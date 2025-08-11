import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/api.tokens';
import { Proveedor, ProveedorCreate, ProveedorPatch } from './proveedor.model';

@Injectable({ providedIn: 'root' })
export class ProveedoresService {
  private http = inject(HttpClient);
  private apiBase = inject(API_BASE_URL);              // centralizado
  private base = `${this.apiBase}/api/proveedores`;    // el service solo concatena el path

  list(): Observable<Proveedor[]> {
    return this.http.get<Proveedor[]>(this.base);
  }

  get(uid: string): Observable<Proveedor> {
    return this.http.get<Proveedor>(`${this.base}/${uid}`);
  }

  create(body: ProveedorCreate): Observable<Proveedor> {
    return this.http.post<Proveedor>(this.base, body);
  }

  update(uid: string, patch: ProveedorPatch): Observable<Proveedor> {
    return this.http.patch<Proveedor>(`${this.base}/${uid}`, patch);
  }

  delete(uid: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${uid}`);
  }
}
