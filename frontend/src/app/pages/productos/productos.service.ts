import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { API_BASE_URL } from '../../core/api.tokens';
import { shareReplay } from 'rxjs/operators';
import { Producto, ProductoCreate, ProductoPatch,ImagenProducto } from './producto.model';

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
  private principalCache = new Map<string, Observable<string | null>>();

absApi(u: string): string {
  if (!u) return u as any;
  if (/^https?:\/\//i.test(u)) return u; // ya es absoluta

  // "base" normalmente termina en ".../api/productos"
  const baseUrl = new URL(this.base, window.location.origin);
  const origin = baseUrl.origin;
  const ctx = baseUrl.pathname
    .replace(/\/api\/productos$/i, '') // quita "/api/productos"
    .replace(/\/api$/i, '');           // o "/api" si fuera el caso

  const prefix = origin + (ctx || '');
  return u.startsWith('/') ? prefix + u : `${prefix}/${u}`;
}


principalImagenUrl(productoUid: string): Observable<string | null> {
  const cached = this.principalCache.get(productoUid);
  if (cached) return cached;

  const obs = this.listImagenes(productoUid).pipe(
    map(imgs => {
      const img = imgs.find(i => i.principal) ?? imgs[0];
 
      return img ? this.absApi(img.url) : null;   // usa absApi para prefijar /omniventas, host, etc.
    }),
    shareReplay(1) // cachea el resultado
  );

  this.principalCache.set(productoUid, obs);



  return obs;


}

clearPrincipalCache(productoUid?: string) {
  if (productoUid) this.principalCache.delete(productoUid);
  else this.principalCache.clear();
}

  resolveImageSrc(img: ImagenProducto): string {
  // si es absoluta (remota), úsala tal cual
  if (img?.url && /^https?:\/\//i.test(img.url)) return img.url;
  // si es relativa de la API (/api/...), prefija context-path y host
  return this.absApi(img.url);
}



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

  listImagenes(productoUid: string) {
  return this.http.get<{success:boolean, data: ImagenProducto[]}>(
    `${this.base}/${productoUid}/imagenes`
  ).pipe(map(res => res.data));
}

uploadImagen(productoUid: string, file: File, principal = false, altText?: string) {
  const fd = new FormData();
  fd.append('file', file, file.name);
  fd.append('principal', String(principal));
  if (altText) fd.append('altText', altText);

  return this.http.post<{success:boolean, data: ImagenProducto}>(
    `${this.base}/${productoUid}/imagenes`, fd
  ).pipe(map(res => res.data));
}

registrarImagenUrl(productoUid: string, url: string, principal = false, altText?: string) {
  return this.http.post<{success:boolean, data: ImagenProducto}>(
    `${this.base}/${productoUid}/imagenes:url`,
    { url, principal, altText }
  ).pipe(map(res => res.data));
}

setImagenPrincipal(productoUid: string, imagenUid: string) {
  return this.http.put<void>(`${this.base}/${productoUid}/imagenes/${imagenUid}/principal`, {});
}

deleteImagen(productoUid: string, imagenUid: string) {
  return this.http.delete<void>(`${this.base}/${productoUid}/imagenes/${imagenUid}`);
}

// Por si quisieras armar URL raw directa:
imageRawUrl(productoUid: string, imagenUid: string) {
  return `${this.base}/${productoUid}/imagenes/${imagenUid}`;
}

getImagenBlob(url: string) {
  // absApi() debe convertir '/api/...' → 'http://host/omniventas/api/...'
  console.log('getImagenBlob', url);
  console.log('base', this.base);
  return this.http.get(this.base+this.absApi(url), { responseType: 'blob' });
}

}
