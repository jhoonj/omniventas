import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Producto } from './producto.model';
import { Observable } from 'rxjs';

const API_BASE = 'http://localhost:8080/optica'; // ajusta
const URL = `${API_BASE}/productos`;

@Injectable({ providedIn: 'root' })
export class ProductosService {
  constructor(private http: HttpClient) {}

  list(): Observable<Producto[]> {
    return this.http.get<Producto[]>(URL);
  }

  get(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${URL}/${id}`);
  }

  create(p: Producto): Observable<Producto> {
    return this.http.post<Producto>(URL, p);
  }

  update(p: Producto): Observable<Producto> {
    if (!p.id) throw new Error('Falta id para actualizar');
    return this.http.post<Producto>(URL, p); // si tu back usa POST upsert
    // o put:
    // return this.http.put<Producto>(`${URL}/${p.id}`, p);
  }

  delete(id: number) {
    return this.http.delete<void>(`${URL}/${id}`);
  }
}
