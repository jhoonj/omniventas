import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/api.tokens';
import { Producto, ProductoCreate, ProductoPatch } from './producto.model';

export interface InventarioSaldo {
  productoUid: string;
  almacenId: string | null;
  saldo: number;
}
export interface StockRes {
  productoUid: string;
  total: number;
  porAlmacen: InventarioSaldo[];
}

export type MovimientoTipo =
  | 'ingreso'
  | 'salida'
  | 'reserva'
  | 'ajuste'
  | 'transferencia_in'
  | 'transferencia_out';

export interface MovimientoInventarioReq {
  tipo: MovimientoTipo;
  cantidad: number;
  referenciaTipo?: string | null;
  referenciaId?: string | null;
  nota?: string | null;
  almacenId?: string | null;
}

@Injectable({ providedIn: 'root' })
export class ProductosService {
  private http = inject(HttpClient);
  private apiBase = inject(API_BASE_URL);
  private base = `${this.apiBase}/api/productos`;

  list(): Observable<Producto[]> {
    var respuesta = this.http.get<Producto[]>(this.base);


    return respuesta;
  }
  get(uid: string): Observable<Producto> {
    return this.http.get<Producto>(`${this.base}/${uid}`);
  }
  create(body: ProductoCreate): Observable<Producto> {
    return this.http.post<Producto>(this.base, body);
  }
  update(uid: string, patch: ProductoPatch): Observable<Producto> {
    return this.http.patch<Producto>(`${this.base}/${uid}`, patch);
  }
  delete(uid: string): Observable<void> {
    return this.http.delete<void>(`${this.base}/${uid}`);
  }

 stock(uid: string): Observable<number> {
    return this.http.get<any>(`${this.base}/${uid}/stock`).pipe(
      map(res => res?.data ?? res),
      map((val: any) => {
        if (val == null) return 0;
        if (typeof val === 'number') return val;
        if (typeof val === 'object') {
          // Ajusta estas llaves al shape real que entregue tu backend
          if ('saldo' in val) return Number(val.saldo ?? 0);
          if ('stock' in val) return Number(val.stock ?? 0);
          if ('cantidad' in val) return Number(val.cantidad ?? 0);
        }
        // fallback robusto
        const n = Number(val);
        return Number.isFinite(n) ? n : 0;
      })
    );
  }
  movimiento(uid: string, req: MovimientoInventarioReq): Observable<StockRes> {
    return this.http.post<StockRes>(`${this.base}/${uid}/movimientos`, req);
  }
}
