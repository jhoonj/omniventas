import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Proveedor } from '../proveedores/proveedor.model';

const API_BASE = '/optica'; // ajusta
const URL = `${API_BASE}/proveedores`;

@Injectable({ providedIn: 'root' })
export class ProveedoresService {
  constructor(private http: HttpClient) {}
  list(): Observable<Proveedor[]> {
    return this.http.get<Proveedor[]>(URL);
  }
}
