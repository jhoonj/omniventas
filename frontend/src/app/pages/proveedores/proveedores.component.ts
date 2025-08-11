import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';

import { ProveedoresService } from './proveedores.service';
import { Proveedor } from './proveedor.model';
import { ProveedorDialogComponent } from './proveedor-dialog.component';

@Component({
  standalone: true,
  selector: 'app-proveedores',
  imports: [
    CommonModule,
    MatTableModule, MatButtonModule, MatIconModule,
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
  cols = ['nombre','email','telefono','direccion','acciones'];

  ngOnInit(): void { this.load(); }

  load() {
    this.svc.list().subscribe({
      next: data => this.proveedores.set(data),
      error: () => this.snack.open('No se pudo cargar proveedores', 'Cerrar', { duration: 3000 })
    });
  }

  openCreate() {
    this.dialog.open(ProveedorDialogComponent, { data: {}, width: '560px' })
      .afterClosed().subscribe(ok => { if (ok) this.load(); });
  }

  openEdit(p: Proveedor) {
    this.dialog.open(ProveedorDialogComponent, { data: { proveedor: p }, width: '560px' })
      .afterClosed().subscribe(ok => { if (ok) this.load(); });
  }

  remove(p: Proveedor) {
    if (!p.uid) return;
    if (!confirm(`¿Eliminar proveedor "${p.nombre}"?`)) return;
    this.svc.delete(p.uid).subscribe({
      next: () => { this.snack.open('Proveedor eliminado', 'OK', { duration: 1800 }); this.load(); },
      error: (err) => this.snack.open(err?.error?.message || 'Error eliminando proveedor', 'Cerrar', { duration: 3000 })
    });
  }
}
