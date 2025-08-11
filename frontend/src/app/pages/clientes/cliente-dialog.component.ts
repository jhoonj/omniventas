import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { Cliente } from './cliente.model';

@Component({
  standalone: true,
  selector: 'app-cliente-dialog',
  imports: [
    CommonModule, ReactiveFormsModule,
    MatDialogModule, MatFormFieldModule, MatInputModule, MatButtonModule
  ],
  template: `
  <h2 mat-dialog-title>{{ data?.cliente?.id ? 'Editar cliente' : 'Nuevo cliente' }}</h2>
  <form [formGroup]="form" class="p-4" (ngSubmit)="save()">
    <mat-form-field appearance="outline" class="w-full">
      <mat-label>Nombre</mat-label>
      <input matInput formControlName="nombre" required>
    </mat-form-field>

    <mat-form-field appearance="outline" class="w-full">
      <mat-label>Email</mat-label>
      <input matInput formControlName="email" type="email">
    </mat-form-field>

    <mat-form-field appearance="outline" class="w-full">
      <mat-label>Teléfono</mat-label>
      <input matInput formControlName="telefono">
    </mat-form-field>

    <mat-form-field appearance="outline" class="w-full">
      <mat-label>Dirección</mat-label>
      <textarea matInput formControlName="direccion" rows="3"></textarea>
    </mat-form-field>

    <div class="flex gap-2 justify-end mt-2">
      <button mat-button type="button" (click)="close()">Cancelar</button>
      <button mat-flat-button color="primary" type="submit" [disabled]="form.invalid">Guardar</button>
    </div>
  </form>
  `,
})
export class ClienteDialogComponent {
  data = inject<{ cliente?: Cliente }>(MAT_DIALOG_DATA);
  ref = inject(MatDialogRef<ClienteDialogComponent>);
  fb = inject(FormBuilder);

  form = this.fb.group({
    id: [this.data?.cliente?.id ?? null],
    nombre: [this.data?.cliente?.nombre ?? '', [Validators.required, Validators.minLength(2)]],
    email: [this.data?.cliente?.email ?? ''],
    telefono: [this.data?.cliente?.telefono ?? ''],
    direccion: [this.data?.cliente?.direccion ?? ''],
  });

  close() { this.ref.close(); }
  save() { if (this.form.valid) this.ref.close(this.form.value as Cliente); }
}
