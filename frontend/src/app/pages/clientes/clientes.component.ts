// src/app/pages/clientes/clientes.component.ts
import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { ClientesService } from './clientes.service';
import { Cliente } from './cliente.model';
import { ClienteDialogComponent } from './cliente-dialog.component';

@Component({
  selector: 'app-clientes',
  standalone: true,
  imports: [
    CommonModule,
    MatTableModule, MatIconModule, MatButtonModule,
    MatDialogModule, MatSnackBarModule
  ],
  templateUrl: './clientes.html',
  styleUrls: ['./clientes.scss']
})
export class ClientesComponent implements OnInit {
  private svc = inject(ClientesService);
  private dialog = inject(MatDialog);
  private snack = inject(MatSnackBar);

  clientes = signal<Cliente[]>([]);
  cols = ['nombre', 'email', 'telefono', 'direccion', 'acciones'];

  ngOnInit() { this.load(); }

  load() {
    this.svc.list().subscribe({
      next: data => this.clientes.set(data),
      error: () => this.snack.open('No se pudo cargar clientes', 'Cerrar', { duration: 3000 })
    });
  }

  openCreate() {
    this.dialog.open(ClienteDialogComponent, { data: {} })
      .afterClosed().subscribe(ok => { if (ok) this.load(); });
  }

  openEdit(c: Cliente) {
    this.dialog.open(ClienteDialogComponent, { data: { cliente: c } })
      .afterClosed().subscribe(ok => { if (ok) this.load(); });
  }

  remove(c: Cliente) {
    if (!c.uid) return;
    if (!confirm(`¿Eliminar cliente "${c.nombre}"?`)) return;
    this.svc.delete(c.uid).subscribe({
      next: () => { this.snack.open('Cliente eliminado', 'OK', { duration: 1800 }); this.load(); },
      error: (err) => this.snack.open(err?.error?.message || 'Error eliminando cliente', 'Cerrar', { duration: 3000 })
    });
  }
}
