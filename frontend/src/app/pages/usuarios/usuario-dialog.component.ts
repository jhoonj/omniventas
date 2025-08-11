import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { RolesService, Rol } from './roles.service';
import { Usuario } from './usuario.model';

@Component({
  selector: 'app-usuario-dialog',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatSelectModule
  ],
  templateUrl: './usuario-dialog.html'
})
export class UsuarioDialogComponent implements OnInit {
  form!: FormGroup;
  roles: Rol[] = [];

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { usuario: Usuario },
    private dialogRef: MatDialogRef<UsuarioDialogComponent>,
    private fb: FormBuilder,
    private rolesService: RolesService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      nombre: [this.data.usuario.nombre || '', Validators.required],
      email: [this.data.usuario.email || '', [Validators.required, Validators.email]],
      contrasenahash: [''], // opcional
      rol_id: [this.data.usuario.rol_id || null, Validators.required]
    });

    this.rolesService.getAll().subscribe(res => this.roles = res);
  }

  save() {
    if (this.form.valid) {
      this.dialogRef.close(this.form.value);
    }
  }

  close() {
    this.dialogRef.close();
  }
}
