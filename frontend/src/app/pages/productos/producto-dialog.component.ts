import { Component, Inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, Validators, FormGroup, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Producto, ProductoCreate, ProductoPatch } from './producto.model';
import { ProductosService } from './productos.service';
import { ProveedoresService } from '../proveedores/proveedores.service';
import { Proveedor } from '../proveedores/proveedor.model';

@Component({
  standalone: true,
  selector: 'app-producto-dialog',
  imports: [
    CommonModule, ReactiveFormsModule,
    MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatProgressBarModule, MatSnackBarModule
  ],
  template: `
  <div class="relative">
    <mat-progress-bar *ngIf="saving()" mode="indeterminate"></mat-progress-bar>
    <h2 mat-dialog-title class="flex items-center gap-2">
      <span class="material-icons">inventory_2</span>
      {{ isEdit() ? 'Editar producto' : 'Nuevo producto' }}
    </h2>

    <mat-dialog-content class="pt-2">
      <form [formGroup]="form" class="grid gap-3" (ngSubmit)="onSubmit()">
        <input type="hidden" formControlName="uid" />

        <mat-form-field appearance="outline">
          <mat-label>Nombre</mat-label>
          <input matInput formControlName="nombre" required maxlength="100" autocomplete="off">
          <mat-hint align="end">{{ form.controls['nombre'].value?.length || 0 }}/100</mat-hint>
          <mat-error *ngIf="c('nombre','required')">El nombre es obligatorio.</mat-error>
          <mat-error *ngIf="c('nombre','minlength')">Mínimo 2 caracteres.</mat-error>
          <mat-error *ngIf="c('nombre','maxlength')">Máximo 100 caracteres.</mat-error>
          <mat-error *ngIf="c('nombre','pattern')">Solo letras, números, espacios y . , - & ( ).</mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Tipo</mat-label>
          <mat-select formControlName="tipo">
            <mat-option [value]="null">—</mat-option>
            <mat-option value="lente">Lente</mat-option>
            <mat-option value="armazon">Armazón</mat-option>
            <mat-option value="accesorio">Accesorio</mat-option>
            <mat-option value="otro">Otro</mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Precio</mat-label>
          <input matInput type="number" formControlName="precio" inputmode="decimal" step="0.01" min="0" required>
          <mat-hint align="start">COP (2 decimales máx.)</mat-hint>
          <mat-error *ngIf="c('precio','required')">El precio es obligatorio.</mat-error>
          <mat-error *ngIf="c('precio','min')">No puede ser negativo.</mat-error>
          <mat-error *ngIf="c('precio','max2dec')">Máximo 2 decimales.</mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Proveedor (opcional)</mat-label>
          <mat-select formControlName="proveedorUid">
            <mat-option [value]="null">—</mat-option>
            <mat-option *ngFor="let p of proveedores; trackBy: trackProv" [value]="p.uid">
              <!-- mostramos id si viene del backend -->
              {{ p.nombre }} <ng-container *ngIf="p.id">(#{{ p.id }})</ng-container>
              <span *ngIf="p.email"> — {{ p.email }}</span>
            </mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Descripción (opcional)</mat-label>
          <textarea matInput formControlName="descripcion" rows="3" maxlength="500"></textarea>
          <mat-hint align="end">{{ form.controls['descripcion'].value?.length || 0 }}/500</mat-hint>
        </mat-form-field>

        <mat-dialog-actions align="end" class="mt-2">
          <button mat-button type="button" (click)="close()" [disabled]="saving()">Cancelar</button>
          <button mat-flat-button color="primary" type="submit" [disabled]="form.invalid || saving()">
            {{ isEdit() ? 'Guardar' : 'Crear' }}
          </button>
        </mat-dialog-actions>
      </form>
    </mat-dialog-content>
  </div>
  `,
  styles: [`
    .material-icons { font-variation-settings: 'FILL' 0, 'wght' 500; }
    .grid { display: grid; grid-template-columns: 1fr; }
  `]
})
export class ProductoDialogComponent {
  form!: FormGroup;
  saving = signal(false);
  proveedores: Proveedor[] = [];

  private nameRegex = /^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9 .,'’&()\-\u00BF\u00A1]+$/; // admite ’ (smart), ¿ ¡

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { producto?: Producto } = {},
    private fb: FormBuilder,
    private ref: MatDialogRef<ProductoDialogComponent>,
    private svc: ProductosService,
    private provSvc: ProveedoresService,
    private snack: MatSnackBar
  ) {
    const p = data?.producto;
    this.form = this.fb.group({
      uid: [p?.uid ?? null],
      nombre: [p?.nombre ?? '', [
        Validators.required, Validators.minLength(2), Validators.maxLength(100),
        Validators.pattern(this.nameRegex)
      ]],
      tipo: [p?.tipo ?? null],
      precio: [p?.precio ?? null, [
        Validators.required, Validators.min(0), this.max2Decimals()
      ]],
      proveedorUid: [p?.proveedorUid ?? null],
      descripcion: [p?.descripcion ?? '', [ Validators.maxLength(500) ]]
    });

    // carga proveedores para el select (ordenados por nombre)
    this.provSvc.list().subscribe({
      next: res => this.proveedores = [...res].sort((a, b) => a.nombre.localeCompare(b.nombre)),
      error: () => { /* opcional: snack */ }
    });
  }

  isEdit(): boolean { return !!this.form.get('uid')?.value; }

  private max2Decimals() {
    return (c: AbstractControl): ValidationErrors | null => {
      const v = c.value;
      if (v === null || v === undefined || v === '') return null;
      const s = String(v);
      return /^\d+(\.\d{1,2})?$/.test(s) ? null : { max2dec: true };
    };
  }

  private normalize() {
    const v = this.form.value as any;
    const t = (s: any) => typeof s === 'string' ? s.trim() : s;
    this.form.patchValue({
      nombre: t(v.nombre),
      descripcion: t(v.descripcion)
    }, { emitEvent: false });
  }

  onSubmit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.normalize();
    this.saving.set(true);

    const { uid, nombre, descripcion, tipo, precio, proveedorUid } = this.form.getRawValue();

    if (this.isEdit()) {
      const base = (this.data?.producto ?? {}) as Producto;
      const patch: ProductoPatch = {};
      if (nombre !== base.nombre) patch.nombre = nombre!;
      if ((descripcion ?? null) !== (base.descripcion ?? null)) patch.descripcion = (descripcion || null) as any;
      if ((tipo ?? null) !== (base.tipo ?? null)) patch.tipo = (tipo || null) as any;
      if (precio !== base.precio) patch.precio = Number(precio);
      if ((proveedorUid ?? null) !== (base.proveedorUid ?? null)) patch.proveedorUid = (proveedorUid || null) as any;

      if (Object.keys(patch).length === 0) { this.saving.set(false); this.ref.close(false); return; }

      this.svc.update(uid!, patch).subscribe({
        next: () => { this.saving.set(false); this.snack.open('Producto actualizado', 'OK', { duration: 1800 }); this.ref.close(true); },
        error: (err) => this.fail(err)
      });
    } else {
      const body: ProductoCreate = {
        nombre: nombre!,
        descripcion: (descripcion || null) as any,
        tipo: (tipo || null) as any,
        precio: Number(precio),
        proveedorUid: (proveedorUid || null) as any
      };
      this.svc.create(body).subscribe({
        next: () => { this.saving.set(false); this.snack.open('Producto creado', 'OK', { duration: 1800 }); this.ref.close(true); },
        error: (err) => this.fail(err)
      });
    }
  }

  private fail(err: any) {
    this.saving.set(false);
    const msg = err?.error?.message || err?.message || 'No se pudo guardar.';
    this.snack.open(msg, 'Cerrar', { duration: 3500 });
  }

  close() { if (!this.saving()) this.ref.close(false); }

  // util para template
  c(ctrl: string, err: string) { return this.form.controls[ctrl].hasError(err); }

  trackProv = (_: number, p: Proveedor) => p.uid;
}
