import { Component, Inject } from '@angular/core';
import { FormBuilder, Validators, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSelectModule } from '@angular/material/select';
import { CommonModule } from '@angular/common';
import { Proveedor } from '../proveedores/proveedor.model';
import { ProveedoresService } from '../proveedores/proveedores.service';
import { Producto } from './producto.model';

@Component({
  selector: 'app-producto-dialog',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, MatDialogModule,
    MatFormFieldModule, MatInputModule, MatButtonModule, MatSelectModule
  ],
  templateUrl: './producto-dialog.html'
})
export class ProductoDialogComponent {
  form: FormGroup;
  proveedores: Proveedor[] = [];

  tipos = ['LENTES', 'MONTURA', 'ACCESORIO', 'SERVICIO']; // ajusta a tus valores válidos

  constructor(
    private fb: FormBuilder,
    private proveedoresSrv: ProveedoresService,
    private dialogRef: MatDialogRef<ProductoDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { producto: Producto }
  ) {
    const p = data.producto || {} as Producto;
    this.form = this.fb.group({
      id: [p.id],
      nombre: [p.nombre || '', [Validators.required, Validators.maxLength(100)]],
      descripcion: [p.descripcion || ''],
      tipo: [p.tipo || ''],
      precio: [p.precio ?? null, [Validators.min(0)]],
      stock: [p.stock ?? 0, [Validators.min(0)]],
      proveedor_id: [p.proveedor_id ?? null, []]
    });

    this.loadProveedores();
  }

  private loadProveedores() {
    this.proveedoresSrv.list().subscribe({
      next: (list) => this.proveedores = list,
      error: () => this.proveedores = []
    });
  }

  save() {
    if (this.form.invalid) return;
    const value: Producto = this.form.value;
    this.dialogRef.close(value);
  }

  close() {
    this.dialogRef.close();
  }
}
