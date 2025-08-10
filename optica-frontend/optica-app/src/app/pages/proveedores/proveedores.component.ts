import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ProveedoresService } from './proveedores.service';
import { Proveedor } from './proveedor.model';
import { ProveedorDialogComponent } from './proveedor-dialog.component';

@Component({
  selector: 'app-proveedores',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule, MatIconModule, MatButtonModule,
    MatDialogModule, MatSnackBarModule
  ],
  templateUrl: './proveedores.html',
  styleUrls: ['./proveedores.scss']
})
export class ProveedoresComponent implements OnInit {
  private svc = inject(ProveedoresService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  proveedores = signal<Proveedor[]>([]);
  cols = ['nombre', 'email', 'telefono', 'direccion', 'acciones'];

  ngOnInit() { this.load(); }

  load() {
    this.svc.list().subscribe({
      next: data => this.proveedores.set(data),
      error: () => this.snack.open('No se pudo cargar proveedores', 'Cerrar', { duration: 3000 })
    });
  }

  openCreate() {
    this.dialog.open(ProveedorDialogComponent, { data: {} })
      .afterClosed().subscribe((p: Proveedor | undefined) => {
        if (!p) return;
        this.svc.save(p).subscribe({
          next: () => { this.snack.open('Proveedor creado', 'OK', { duration: 2000 }); this.load(); },
          error: () => this.snack.open('Error creando proveedor', 'Cerrar', { duration: 3000 })
        });
      });
  }

openEdit(p: Proveedor) {
  this.dialog.open(ProveedorDialogComponent, { data: { proveedor: p } })
    .afterClosed()
    .subscribe((upd: Proveedor | undefined) => {
      if (!upd || !p.id) return;

      // Mezcla lo editado con el id original
      const body: Proveedor = { ...p, ...upd, id: p.id };

      this.svc.save(body).subscribe({
        next: () => { 
          this.snack.open('Proveedor actualizado', 'OK', { duration: 2000 }); 
          this.load(); 
        },
        error: () => this.snack.open('Error actualizando', 'Cerrar', { duration: 3000 })
      });
    });
}

  remove(p: Proveedor) {
    if (!p.id) return;
    if (!confirm(`¿Eliminar proveedor "${p.nombre}"?`)) return;
    this.svc.delete(p.id).subscribe({
      next: () => { this.snack.open('Proveedor eliminado', 'OK', { duration: 2000 }); this.load(); },
      error: () => this.snack.open('Error eliminando', 'Cerrar', { duration: 3000 })
    });
  }
}
