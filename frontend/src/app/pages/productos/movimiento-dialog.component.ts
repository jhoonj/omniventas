import { Component, Inject, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NonNullableFormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import {
  ProductosService,
  MovimientoInventarioReq,
  MovimientoTipo,
  StockRes
} from './productos.service';

@Component({
  standalone: true,
  selector: 'app-movimiento-dialog',
  imports: [
    CommonModule, ReactiveFormsModule,
    MatDialogModule, MatFormFieldModule, MatSelectModule, MatInputModule,
    MatButtonModule, MatProgressBarModule, MatSnackBarModule
  ],
  template: `
  <div class="relative">
    <mat-progress-bar *ngIf="saving()" mode="indeterminate"></mat-progress-bar>
    <h2 mat-dialog-title class="flex items-center gap-2">
      <span class="material-icons">swap_vert</span>
      Movimiento de inventario
    </h2>
    <mat-dialog-content>
      <div class="text-sm text-gray-600 mb-2">Stock actual: <b>{{ stock?.total ?? '—' }}</b></div>

      <form [formGroup]="form" class="grid gap-3" (ngSubmit)="onSubmit()">
        <mat-form-field appearance="outline">
          <mat-label>Tipo</mat-label>
          <mat-select formControlName="tipo" required>
            <mat-option value="ingreso">Ingreso</mat-option>
            <mat-option value="salida">Salida</mat-option>
            <mat-option value="reserva">Reserva</mat-option>
            <mat-option value="ajuste">Ajuste</mat-option>
            <mat-option value="transferencia_in">Transferencia (entrada)</mat-option>
            <mat-option value="transferencia_out">Transferencia (salida)</mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Cantidad</mat-label>
          <input matInput type="number" formControlName="cantidad" inputmode="numeric" min="1" required>
          <mat-hint align="start">Entero positivo</mat-hint>
          <mat-error *ngIf="form.controls.cantidad.hasError('required')">Requerido.</mat-error>
          <mat-error *ngIf="form.controls.cantidad.hasError('min')">Debe ser ≥ 1.</mat-error>
        </mat-form-field>

        <mat-form-field class="w-full">
          <mat-label>Nota (opcional)</mat-label>
          <textarea matInput cdkTextareaAutosize formControlName="nota" rows="2"></textarea>
          <mat-hint align="end">{{ form.controls.nota.value.length || 0 }}/120</mat-hint>
        </mat-form-field>

        <mat-dialog-actions align="end" class="mt-2">
          <button mat-button type="button" (click)="close()" [disabled]="saving()">Cancelar</button>
          <button mat-flat-button color="primary" type="submit" [disabled]="form.invalid || saving()">Aplicar</button>
        </mat-dialog-actions>
      </form>
    </mat-dialog-content>
  </div>
  `
})
export class MovimientoDialogComponent {
  private fb = inject(NonNullableFormBuilder);
  private ref = inject(MatDialogRef<MovimientoDialogComponent>);
  private svc = inject(ProductosService);
  private snack = inject(MatSnackBar);

  saving = signal(false);
  stock?: StockRes;

  form = this.fb.group({
    tipo: this.fb.control<MovimientoTipo>('ingreso', { validators: [Validators.required] }),
    cantidad: this.fb.control<number>(1, { validators: [Validators.required, Validators.min(1)] }),
    nota: this.fb.control<string>('')
  });

  constructor(@Inject(MAT_DIALOG_DATA) public data: { productoUid: string, stock?: StockRes }) {
    this.stock = data?.stock;
  }

  onSubmit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.saving.set(true);

    const req: MovimientoInventarioReq = {
      tipo: this.form.controls.tipo.value,          // ✅ ahora es un literal union
      cantidad: this.form.controls.cantidad.value,
      nota: this.form.controls.nota.value || null
    };

    this.svc.movimiento(this.data.productoUid, req).subscribe({
      next: res => { this.saving.set(false); this.snack.open('Movimiento aplicado', 'OK', { duration: 1600 }); this.ref.close(res); },
      error: err => { this.saving.set(false); this.snack.open(err?.error?.message || 'Error aplicando movimiento', 'Cerrar', { duration: 3200 }); }
    });
  }

  close() { if (!this.saving()) this.ref.close(false); }
}
