import { Component, OnInit, inject, signal } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { ProductosService } from './productos.service';
import { ProveedoresService } from '../proveedores/proveedores.service';
import { Producto } from './producto.model';
import { Proveedor } from '../proveedores/proveedor.model';
import { ProductoDialogComponent } from './producto-dialog.component';
import { MovimientoDialogComponent } from './movimiento-dialog.component';

// ⬇️ si tu componente es standalone, asegúrate de tener estos imports
// (si usas NgModule, ve a la sección 3)
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatChipsModule } from '@angular/material/chips';
import { ImagenProducto } from './producto.model'; // por si falta




@Component({
  // ⬇️ si NO eres standalone, quita estas 4 líneas (standalone/imports)
  standalone: true,
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatDialogModule, MatSnackBarModule, MatChipsModule],
  selector: 'app-productos',
  templateUrl: './productos.html',
  styleUrls: ['./productos.scss']
})
export class ProductosComponent implements OnInit {
  // añadimos la columna 'stock'
  cols = ['imagen', 'nombre', 'tipo', 'precio', 'proveedor', 'stock', 'acciones'];

  private productosSrv = inject(ProductosService);
  private proveedoresSrv = inject(ProveedoresService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  blobSrc: Record<string, string> = {};

  private _productos = signal<(Producto & { proveedorNombre?: string; stock?: number })[]>([]);
  productos = this._productos.asReadonly();
  loading = signal(false);

  constructor(private svc: ProductosService) { }

  ngOnInit(): void {
    this.cargar();
  }

  thumb$(uid: string): Observable<string | null> {
    return this.svc.principalImagenUrl(uid);
  }
  cargar(): void {
    this.loading.set(true);

    forkJoin({
      productos: this.productosSrv.list(),
      proveedores: this.proveedoresSrv.list()
    })
      .pipe(
        map(({ productos, proveedores }) => {
          // mapas por UID e ID del proveedor, por si tu backend envía cualquiera
          const mapUidToName = new Map<string, string>();
          const mapIdToName = new Map<number, string>();
          (proveedores as Proveedor[]).forEach(p => {
            if (p.uid) mapUidToName.set(p.uid, p.nombre);
            if (p.id != null) mapIdToName.set(p.id, p.nombre);
          });

          const enriched = (productos as any[]).map(p => {
            const proveedorNombre =
              (p.proveedorUid ? mapUidToName.get(p.proveedorUid) : undefined) ??
              (p.proveedorId != null ? mapIdToName.get(p.proveedorId) : undefined) ??
              '—';
            return { ...p, proveedorNombre } as Producto & { proveedorNombre?: string };
          });

          return enriched;
        })
      )
      .subscribe({
        next: arr => this._productos.set(arr),
        error: err => {
          const msg = err?.error?.message || 'No se pudo cargar productos';
          this.snack.open(msg, 'Cerrar', { duration: 3000 });
        },
        complete: () => this.loading.set(false)
      });
  }

  // color del chip según stock
  stockColor(p: { stock?: number }): 'primary' | 'accent' | 'warn' {
    const s = p.stock ?? 0;
    if (s <= 0) return 'warn';
    if (s <= 5) return 'accent';
    return 'primary';
  }

  openCreate() {
    const ref = this.dialog.open(ProductoDialogComponent, { width: '640px' });
    ref.afterClosed().subscribe(ok => ok && this.cargar());
  }

  openEdit(p: Producto) {
    const ref = this.dialog.open(ProductoDialogComponent, { width: '640px', data: { producto: p } });
    ref.afterClosed().subscribe(ok => ok && this.cargar());
  }

  verStock(p: Producto) {
    this.productosSrv.stock(p.uid).subscribe({
      next: (res) => {
        this.dialog.open(MovimientoDialogComponent, {
          width: '560px',
          data: { productoUid: p.uid, stock: res }
        }).afterClosed().subscribe(ok => ok && this.cargar());
      },
      error: (err) => this.snack.open(err?.error?.message || 'No se pudo consultar stock', 'Cerrar', { duration: 3000 })
    });
  }

  remove(p: Producto) {
    if (!confirm(`¿Eliminar producto "${p.nombre}"?`)) return;
    this.productosSrv.delete(p.uid).subscribe({
      next: () => { this.snack.open('Producto eliminado', 'OK', { duration: 1800 }); this.cargar(); },
      error: (err) => this.snack.open(err?.error?.message || 'Error eliminando producto', 'Cerrar', { duration: 3000 })
    });
  }

  ngOnDestroy() {
    Object.values(this.blobSrc).forEach(u => URL.revokeObjectURL(u));
  }


  onImgError(img: ImagenProducto) {
    this.svc.getImagenBlob(img.url).subscribe({
      next: blob => {
        // revoca anterior si existía
        const prev = this.blobSrc[img.uid];
        if (prev) URL.revokeObjectURL(prev);
        this.blobSrc[img.uid] = URL.createObjectURL(blob);
      },
      error: err => console.warn('[IMG] blob error', err)
    });
  }

}
