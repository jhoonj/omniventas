import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { Usuario } from './usuario.model';
import { UsuariosService } from './usuarios.service';
import { UsuarioDialogComponent } from './usuario-dialog.component';

@Component({
  selector: 'app-usuarios',
  standalone: true,
  imports: [
    CommonModule, MatTableModule, MatButtonModule, MatIconModule,
    MatSnackBarModule, MatDialogModule
  ],
  templateUrl: './usuarios.html'
})
export class UsuariosComponent implements OnInit {
  displayedColumns = ['id', 'nombre', 'email', 'rol', 'acciones'];
  data: Usuario[] = [];

  constructor(
    private svc: UsuariosService,
    private snack: MatSnackBar,
    private dialog: MatDialog
  ) {}

  ngOnInit() {
    this.load();
  }

  load() {
    this.svc.getAll().subscribe({
      next: res => this.data = res,
      error: () => this.snack.open('Error cargando usuarios', 'Cerrar', { duration: 3000 })
    });
  }

  openCreate() {
    this.dialog.open(UsuarioDialogComponent, { data: { usuario: {} } })
      .afterClosed().subscribe((res: Usuario | undefined) => {
        if (res) {
          this.svc.save(res).subscribe({
            next: () => { this.snack.open('Usuario creado', 'OK', { duration: 2000 }); this.load(); },
            error: () => this.snack.open('Error creando usuario', 'Cerrar', { duration: 3000 })
          });
        }
      });
  }

  openEdit(u: Usuario) {
    this.dialog.open(UsuarioDialogComponent, { data: { usuario: { ...u } } })
      .afterClosed().subscribe((res: Usuario | undefined) => {
        if (res && u.id) {
          res.id = u.id; // asegurar ID para update
          this.svc.save(res).subscribe({
            next: () => { this.snack.open('Usuario actualizado', 'OK', { duration: 2000 }); this.load(); },
            error: () => this.snack.open('Error actualizando usuario', 'Cerrar', { duration: 3000 })
          });
        }
      });
  }

  delete(u: Usuario) {
    if (!u.id) return;
    this.svc.delete(u.id).subscribe({
      next: () => { this.snack.open('Usuario eliminado', 'OK', { duration: 2000 }); this.load(); },
      error: () => this.snack.open('Error eliminando usuario', 'Cerrar', { duration: 3000 })
    });
  }
}
