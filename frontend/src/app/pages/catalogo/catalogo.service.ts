// src/app/pages/catalogo/catalogo.service.ts
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { map, tap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { ApiEnvelope, ImagenProducto, Producto } from './models';
import { environment } from '../../../environments/environment';

type Page<T> = {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number; // page index
  size: number;
};

@Injectable({ providedIn: 'root' })
export class CatalogoService {
  private http = inject(HttpClient);
  private api = environment.apiBase?.replace(/\/$/, '') || '';

  /** ========= Productos ========= */

  /** GET /api/productos (público) – lista simple */
  listarProductos(): Observable<Producto[]> {
    return this.http.get<Producto[] | ApiEnvelope<Producto[]>>(`${this.api}/api/productos`).pipe(
      map((res: any) => (Array.isArray(res) ? (res as Producto[]) : (res?.data ?? [])))
    );
  }

  /** GET /api/productos (público) – paginado/filtrado opcional */
  listarProductosPaginado(opts?: {
    page?: number;
    size?: number;
    q?: string;
    tipo?: string;
    sort?: 'precio,asc' | 'precio,desc' | 'creado,desc';
    minPrecio?: number;
    maxPrecio?: number;
  }): Observable<Page<Producto>> {
    let params = new HttpParams();
    if (opts?.page != null) params = params.set('page', String(opts.page));
    if (opts?.size != null) params = params.set('size', String(opts.size));
    if (opts?.q) params = params.set('q', opts.q);
    if (opts?.tipo) params = params.set('tipo', opts.tipo);
    if (opts?.sort) params = params.set('sort', opts.sort);
    if (opts?.minPrecio != null) params = params.set('minPrecio', String(opts.minPrecio));
    if (opts?.maxPrecio != null) params = params.set('maxPrecio', String(opts.maxPrecio));

    return this.http.get<Page<Producto> | ApiEnvelope<Page<Producto>>>(`${this.api}/api/productos`, { params }).pipe(
      map((res: any) => (res?.data ? (res.data as Page<Producto>) : (res as Page<Producto>)))
    );
  }

  /** GET /api/productos/{uid} – detalle (compat con ApiEnvelope o directo) */
  obtenerProducto(uid: string): Observable<Producto> {
    const url = `${this.api}/api/productos/${encodeURIComponent(uid)}`;
    return this.http.get<any>(url).pipe(
      map((res) => (res && typeof res === 'object' && 'data' in res ? (res.data as Producto) : (res as Producto)))
    );
  }

  /** ========= Imágenes ========= */

  /** GET /api/productos/{uid}/imagenes – lista (puede venir plano o envuelto) */
  listarImagenes(productoUid: string): Observable<ImagenProducto[]> {
    const url = `${this.api}/api/productos/${encodeURIComponent(productoUid)}/imagenes`;
    return this.http.get<any>(url).pipe(
      map((res) => {
        if (Array.isArray(res)) return res as ImagenProducto[];
        if (res && 'data' in res) return (res.data as ImagenProducto[]) ?? [];
        return [];
      })
    );
  }

  /** Alias de compatibilidad (versión anterior) */
  listarImagenesDeProducto(uid: string): Observable<ImagenProducto[]> {
    return this.listarImagenes(uid);
  }

  /** URL directa al RAW de una imagen */
  getImagenRawUrl(productoUid: string, imagenUid: string): string {
    // Según tu backend: GET /api/productos/{productoUid}/imagenes/{imagenUid}/raw (permitAll)
    return `${this.api}/api/productos/${encodeURIComponent(productoUid)}/imagenes/${encodeURIComponent(imagenUid)}/raw`;
  }

  /** Devuelve (y cachea) la URL absoluta de la imagen principal de un producto */
  private imgCache = new Map<string, string>();
  getImagenPrincipalUrl(productoUid: string): Observable<string | null> {
    const cached = this.imgCache.get(productoUid);
    if (cached) return of(cached);

    return this.listarImagenes(productoUid).pipe(
      map((imgs) => {
        if (!imgs?.length) return null;
        const principal = imgs.find((i: ImagenProducto) => i.principal) ?? imgs[0];
        return principal ? this.absApi(principal.url) : null;
      }),
      tap((url) => {
        if (url) this.imgCache.set(productoUid, url);
      })
    );
  }

  /** ========= Utilidades ========= */

  /** Normaliza rutas relativas -> absolutas contra environment.apiBase */
  absApi(path: string | null | undefined): string {
    if (!path) return '';
    if (/^(http|https):\/\//i.test(path)) return path; // ya absoluto
    const left = String(path).replace(/^\//, '');
    return `${this.api}/${left}`;
  }
}
