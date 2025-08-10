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
      .afterClosed().subscribe((c: Cliente | undefined) => {
        if (!c) return;
        this.svc.save(c).subscribe({
          next: () => { this.snack.open('Cliente creado', 'OK', { duration: 2000 }); this.load(); },
          error: () => this.snack.open('Error creando cliente', 'Cerrar', { duration: 3000 })
        });
      });
  }

  openEdit(c: Cliente) {
    this.dialog.open(ClienteDialogComponent, { data: { cliente: c } })
      .afterClosed().subscribe((upd: Cliente | undefined) => {
        if (!upd) return;
        const body: Cliente = { ...c, ...upd, id: c.id };
        this.svc.save(body).subscribe({
          next: () => { this.snack.open('Cliente actualizado', 'OK', { duration: 2000 }); this.load(); },
          error: () => this.snack.open('Error actualizando cliente', 'Cerrar', { duration: 3000 })
        });
      });
  }

  remove(c: Cliente) {
    if (!c.id) return;
    if (!confirm(`¿Eliminar cliente "${c.nombre}"?`)) return;
    this.svc.delete(c.id).subscribe({
      next: () => { this.snack.open('Cliente eliminado', 'OK', { duration: 2000 }); this.load(); },
      error: () => this.snack.open('Error eliminando cliente', 'Cerrar', { duration: 3000 })
    });
  }
}
