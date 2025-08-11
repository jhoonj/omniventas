import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { Producto } from './producto.model';
import { ProductosService } from './productos.service';
import { ProductoDialogComponent } from './producto-dialog.component';
import { MatTableDataSource } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialogModule } from '@angular/material/dialog';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { CurrencyPipe, CommonModule } from '@angular/common';


@Component({
  selector: 'app-productos',
   standalone: true,
  imports: [MatTableModule, MatButtonModule, MatIconModule, MatDialogModule, MatSnackBarModule,CurrencyPipe, CommonModule],
  templateUrl: './productos.html'
})
export class ProductosComponent implements OnInit {
  displayedColumns = ['id', 'nombre', 'tipo', 'precio', 'stock', 'proveedor', 'acciones'];
  data = new MatTableDataSource<Producto>([]);

  constructor(
    private productos: ProductosService,
    private dialog: MatDialog,
    private snack: MatSnackBar
  ) {}

  ngOnInit() { this.load(); }

  load() {
    this.productos.list().subscribe({
      next: items => this.data.data = items,
      error: () => this.snack.open('Error cargando productos', 'Cerrar', { duration: 2500 })
    });
  }

  openCreate() {
    const ref = this.dialog.open(ProductoDialogComponent, {
      width: '600px',
      data: { producto: {} }
    });

    ref.afterClosed().subscribe((value?: Producto) => {
      if (!value) return;
      this.productos.create(value).subscribe({
        next: () => { this.snack.open('Producto creado', 'OK', { duration: 2000 }); this.load(); },
        error: () => this.snack.open('Error al crear', 'Cerrar', { duration: 2500 })
      });
    });
  }

  openEdit(row: Producto) {
    const ref = this.dialog.open(ProductoDialogComponent, {
      width: '600px',
      data: { producto: row }
    });

    ref.afterClosed().subscribe((value?: Producto) => {
      if (!value) return;
      // asegura que conserve el id
      value.id = row.id;
      this.productos.update(value).subscribe({
        next: () => { this.snack.open('Producto actualizado', 'OK', { duration: 2000 }); this.load(); },
        error: () => this.snack.open('Error al actualizar', 'Cerrar', { duration: 2500 })
      });
    });
  }

  delete(row: Producto) {
    if (!row.id) return;
    if (!confirm(`¿Eliminar producto #${row.id}?`)) return;
    this.productos.delete(row.id).subscribe({
      next: () => { this.snack.open('Producto eliminado', 'OK', { duration: 2000 }); this.load(); },
      error: () => this.snack.open('Error al eliminar', 'Cerrar', { duration: 2500 })
    });
  }
}
