import { Component, Inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormBuilder, Validators, FormGroup, ReactiveFormsModule,
  AbstractControl, ValidationErrors, FormControl
} from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatCardModule } from '@angular/material/card';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import {  inject } from '@angular/core';
import { Producto, ProductoCreate, ProductoPatch, ImagenProducto } from './producto.model';
import { ProductosService } from './productos.service';
import { ProveedoresService } from '../proveedores/proveedores.service';
import { Proveedor } from '../proveedores/proveedor.model';
import { API_BASE_URL } from '../../core/api.tokens';

@Component({
  standalone: true,
  selector: 'app-producto-dialog',
  imports: [
    CommonModule, ReactiveFormsModule,
    MatDialogModule, MatFormFieldModule, MatInputModule, MatSelectModule,
    MatButtonModule, MatProgressBarModule, MatSnackBarModule,
    MatCardModule, MatSlideToggleModule, MatChipsModule, MatDividerModule
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

      <!-- Imágenes (solo en edición, cuando ya hay UID) -->
      <ng-container *ngIf="productoUid as uid">
        <mat-divider class="my-4"></mat-divider>

        <h3 class="text-lg font-medium mb-2">Imágenes 3</h3>

        <div class="flex items-end gap-3 mb-3" style="display:flex; align-items:end; gap:12px; margin-bottom:12px;">
          <button mat-stroked-button (click)="fileInput.click()" [disabled]="uploading">
            Subir imagen
          </button>
          <mat-slide-toggle [formControl]="principalCtrl">Marcar como principal</mat-slide-toggle>
          <mat-form-field appearance="outline" class="flex-1" style="flex:1;">
            <mat-label>Alt text (opcional)</mat-label>
            <input matInput [formControl]="altTextCtrl" maxlength="200">
            <mat-hint align="end">{{ (altTextCtrl.value || '').length }}/200</mat-hint>
          </mat-form-field>
          <input #fileInput type="file" accept="image/*" class="hidden" (change)="onFileSelected($event)">
        </div>

        <mat-progress-bar *ngIf="uploading || loadingImgs" mode="indeterminate"></mat-progress-bar>

        <div class="grid gap-3" style="display:grid; gap:12px; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));">
          <mat-card *ngFor="let img of imagenes; trackBy: trackImg" class="overflow-hidden">
            <img [src]="prefix(this.apiBase+img.url)"
                 [alt]="img.altText || img.filename || 'imagen'"
                 style="width:100%; height:160px; object-fit:cover;">
            <mat-card-content>
              <div class="text-sm" style="font-size:12px; white-space:nowrap; overflow:hidden; text-overflow:ellipsis;"
                   [title]="img.filename || img.url">
                {{ img.filename || img.url }}
              </div>
              <div class="text-xs" style="font-size:11px; color:#6b7280;">
                {{ img.sizeBytes | number }} bytes
              </div>
              <mat-chip-set>
                <mat-chip *ngIf="img.principal" color="primary" selected disableRipple>Principal</mat-chip>
              </mat-chip-set>
            </mat-card-content>
            <mat-card-actions align="end">
              <a mat-button [href]="prefix(img.url)" target="_blank" rel="noopener">Abrir</a>
              <button mat-button color="primary" (click)="marcarPrincipal(img)" [disabled]="img.principal">Principal</button>
              <button mat-button color="warn" (click)="eliminarImagen(img)">Eliminar</button>
            </mat-card-actions>
          </mat-card>
        </div>
      </ng-container>
      <!-- /Imágenes -->
    </mat-dialog-content>
  </div>
  `,
  styles: [`
    .material-icons { font-variation-settings: 'FILL' 0, 'wght' 500; }
    .grid { display: grid; grid-template-columns: 1fr; }
    .hidden { display: none; }
  `]
})
export class ProductoDialogComponent {
  form!: FormGroup;
  saving = signal(false);
  proveedores: Proveedor[] = [];
  public apiBase = inject(API_BASE_URL);
  public base = `${this.apiBase}/api/productos`;
  // --- imágenes ---
  imagenes: ImagenProducto[] = [];
  loadingImgs = false;
  uploading = false;
  principalCtrl = new FormControl<boolean>(false, { nonNullable: true });
  altTextCtrl = new FormControl<string | null>(null);

  public blobSrc: Record<string, string> = {};

  
  // Context-path de la API (ajusta si cambia)
  private readonly ctx = '/omniventas';

  private nameRegex = /^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ0-9 .,'’&()\-\u00BF\u00A1]+$/;

  constructor(
    
    @Inject(MAT_DIALOG_DATA) public data: { producto?: Producto } = {},
    private fb: FormBuilder,
    private ref: MatDialogRef<ProductoDialogComponent>,
    public svc: ProductosService,
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

    // si estamos editando, cargar imágenes
    if (this.productoUid) {
      this.cargarImagenes();
    }
  }

  // UID del producto en edición
  get productoUid(): string | null {
    // data.producto?.uid es lo más confiable en este diálogo
    return this.data?.producto?.uid ?? this.form?.value?.uid ?? null;
  }

  // prefija el context-path si la URL es relativa /api/...
  prefix(u?: string | null): string | null {
    if (!u) return u as any;
    return u.startsWith('/api/') ? this.ctx + u : u;
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
        next: () => {
          this.saving.set(false);
          this.snack.open('Producto actualizado', 'OK', { duration: 1800 });
          this.ref.close(true);
        },
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
        next: () => {
          this.saving.set(false);
          this.snack.open('Producto creado', 'OK', { duration: 1800 });
          this.ref.close(true);
        },
        error: (err) => this.fail(err)
      });
    }
  }

  // ---- imágenes ----
  cargarImagenes() {
    const uid = this.productoUid;
    if (!uid) return;
    this.loadingImgs = true;
    this.svc.listImagenes(uid).subscribe({
      next: imgs => this.imagenes = imgs,
      error: err => {
        console.error('[IMG] listImagenes error', err);
        this.snack.open('No se pudieron cargar las imágenes', 'Cerrar', { duration: 3000 });
      },
      complete: () => this.loadingImgs = false
    });
  }

  onFileSelected(ev: Event) {
    const input = ev.target as HTMLInputElement;
    if (!input.files || !input.files.length || !this.productoUid) return;
    const file = input.files[0];

    this.uploading = true;
    this.svc.uploadImagen(
      this.productoUid,
      file,
      !!this.principalCtrl.value,
      this.altTextCtrl.value ?? undefined
    ).subscribe({
      next: _img => {
        this.snack.open('Imagen subida', 'OK', { duration: 1800 });
        this.principalCtrl.setValue(false);
        this.altTextCtrl.setValue(null);
        (input as any).value = null;
        this.cargarImagenes();
      },
      error: err => {
        console.error('[IMG] upload error', err);
        const msg = err?.error?.error?.message || 'Error subiendo imagen';
        this.snack.open(msg, 'Cerrar', { duration: 4000 });
      },
      complete: () => this.uploading = false
    });
  }

  marcarPrincipal(img: ImagenProducto) {
    if (!this.productoUid) return;
    this.svc.setImagenPrincipal(this.productoUid, img.uid).subscribe({
      next: () => { this.snack.open('Marcada como principal', 'OK', { duration: 1500 }); this.cargarImagenes(); },
      error: () => this.snack.open('No se pudo marcar como principal', 'Cerrar', { duration: 3000 })
    });
  }

  eliminarImagen(img: ImagenProducto) {
    if (!this.productoUid) return;
    this.svc.deleteImagen(this.productoUid, img.uid).subscribe({
      next: () => { this.snack.open('Imagen eliminada', 'OK', { duration: 1500 }); this.cargarImagenes(); },
      error: () => this.snack.open('No se pudo eliminar la imagen', 'Cerrar', { duration: 3000 })
    });
  }


  trackImg = (_: number, img: ImagenProducto) => img.uid;

  // ---- util ----
  private fail(err: any) {
    this.saving.set(false);
    const msg = err?.error?.message || err?.message || 'No se pudo guardar.';
    this.snack.open(msg, 'Cerrar', { duration: 3500 });
  }

  close() { if (!this.saving()) this.ref.close(false); }
  c(ctrl: string, err: string) { return this.form.controls[ctrl].hasError(err); }
  trackProv = (_: number, p: Proveedor) => p.uid;



ngOnDestroy() {
  Object.values(this.blobSrc).forEach(u => URL.revokeObjectURL(u));
}

getImgSrc(img: ImagenProducto): string {
  const finalSrc = this.blobSrc[img.uid] ?? this.svc.absApi(img.url);
  console.log('[IMG SRC]', { uid: img.uid, raw: img.url, final: finalSrc, viaBlob: !!this.blobSrc[img.uid] });
  return finalSrc;
}


onImgError(img: ImagenProducto) {
  console.warn('[IMG ERROR] fallback a blob:', img.uid, img.url);
  this.svc.getImagenBlob(img.url).subscribe({
    next: blob => {
      const prev = this.blobSrc[img.uid]; if (prev) URL.revokeObjectURL(prev);
      this.blobSrc[img.uid] = URL.createObjectURL(blob);
      console.log('[IMG BLOB OK]', img.uid, this.blobSrc[img.uid]);
    },
    error: err => console.error('[IMG BLOB FAIL]', err)
  });
}

onImgLoad(img: ImagenProducto) {
  console.log('[IMG LOAD OK]', img.uid);
}

}


