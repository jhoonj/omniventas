// src/app/pages/clientes/cliente-dialog.component.ts
import { Component, Inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Cliente, ClienteCreate, ClientePatch } from './cliente.model';
import { ClientesService } from './clientes.service';

@Component({
  standalone: true,
  selector: 'app-cliente-dialog',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressBarModule,
    MatSnackBarModule
  ],
  template: `
  <div class="relative">
    <mat-progress-bar *ngIf="saving()" mode="indeterminate"></mat-progress-bar>

    <h2 mat-dialog-title class="flex items-center gap-2">
      <span class="material-icons">person</span>
      {{ isEdit() ? 'Editar cliente' : 'Nuevo cliente' }}
    </h2>

    <mat-dialog-content class="pt-2">
      <form [formGroup]="form" class="grid gap-3" (ngSubmit)="onSubmit()">
        <input type="hidden" formControlName="uid" />

        <mat-form-field appearance="outline">
          <mat-label>Nombre</mat-label>
          <input matInput formControlName="nombre" required maxlength="100" autocomplete="name">
          <mat-hint align="end">{{ form.controls['nombre'].value?.length || 0 }}/100</mat-hint>
          <mat-error *ngIf="form.controls['nombre'].hasError('required')">El nombre es obligatorio.</mat-error>
          <mat-error *ngIf="form.controls['nombre'].hasError('minlength')">Mínimo 2 caracteres.</mat-error>
          <mat-error *ngIf="form.controls['nombre'].hasError('maxlength')">Máximo 100 caracteres.</mat-error>
          <mat-error *ngIf="form.controls['nombre'].hasError('pattern')">Solo letras, espacios, apóstrofo y guion.</mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Email</mat-label>
          <input matInput formControlName="email" required maxlength="100" autocomplete="email">
          <mat-hint align="end">{{ form.controls['email'].value?.length || 0 }}/100</mat-hint>
          <mat-error *ngIf="form.controls['email'].hasError('required')">El email es obligatorio.</mat-error>
          <mat-error *ngIf="form.controls['email'].hasError('email')">El formato no es válido.</mat-error>
          <mat-error *ngIf="form.controls['email'].hasError('maxlength')">Máximo 100 caracteres.</mat-error>
          <mat-error *ngIf="form.controls['email'].hasError('duplicate')">Este email ya está registrado.</mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Teléfono</mat-label>
          <input
            matInput formControlName="telefono"
            inputmode="numeric" pattern="\\d*"
            minlength="7" maxlength="15"
            (keypress)="onlyDigits($event)"
            autocomplete="tel">
          <mat-hint align="start">Solo dígitos (7–15)</mat-hint>
          <mat-hint align="end">{{ form.controls['telefono'].value?.length || 0 }}/15</mat-hint>
          <mat-error *ngIf="form.controls['telefono'].hasError('pattern')">Solo números.</mat-error>
          <mat-error *ngIf="form.controls['telefono'].hasError('minlength')">Mínimo 7 dígitos.</mat-error>
          <mat-error *ngIf="form.controls['telefono'].hasError('maxlength')">Máximo 15 dígitos.</mat-error>
        </mat-form-field>

        <mat-form-field appearance="outline">
          <mat-label>Dirección</mat-label>
          <textarea matInput formControlName="direccion" rows="3" maxlength="255" autocomplete="street-address"></textarea>
          <mat-hint align="end">{{ form.controls['direccion'].value?.length || 0 }}/255</mat-hint>
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
    .material-icons { font-variation-settings: 'FILL' 0, 'wght' 500, 'GRAD' 0, 'opsz' 24; }
    .grid { display: grid; grid-template-columns: 1fr; }
    @media (min-width: 640px) {
      .grid { grid-template-columns: 1fr; }
    }
  `]
})
export class ClienteDialogComponent {
  form!: FormGroup;               // <- se inicializa en el constructor
  saving = signal(false);

  // Regex de nombre (letras, espacios, apóstrofo y guion)
  private nameRegex = /^[A-Za-zÁÉÍÓÚÜÑáéíóúüñ'’\- ]+$/;

  constructor(
    @Inject(MAT_DIALOG_DATA) public data: { cliente?: Cliente } = {},
    private fb: FormBuilder,
    private ref: MatDialogRef<ClienteDialogComponent>,
    private svc: ClientesService,
    private snack: MatSnackBar
  ) {
    const c = data?.cliente;
    this.form = this.fb.group({
      uid: [c?.uid ?? null],
      nombre: [c?.nombre ?? '', [
        Validators.required,
        Validators.minLength(2),
        Validators.maxLength(100),
        Validators.pattern(this.nameRegex)
      ]],
      email: [c?.email ?? '', [
        Validators.required,
        Validators.email,
        Validators.maxLength(100)
      ]],
      telefono: [c?.telefono ?? '', [
        Validators.minLength(7),
        Validators.maxLength(15),
        Validators.pattern(/^\d*$/)
      ]],
      direccion: [c?.direccion ?? '', [
        Validators.maxLength(255)
      ]]
    });
  }

  isEdit(): boolean {
    return !!this.form.get('uid')?.value;
  }

  onlyDigits(evt: KeyboardEvent) {
    const ok = /\d/.test(evt.key);
    if (!ok) evt.preventDefault();
  }

  private normalize() {
    const norm = (s: any) => (typeof s === 'string' ? s.trim() : s);
    this.form.patchValue({
      nombre: norm(this.form.value['nombre']),
      email: norm(this.form.value['email']),
      telefono: norm(this.form.value['telefono']),
      direccion: norm(this.form.value['direccion'])
    }, { emitEvent: false });
  }

  onSubmit() {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.normalize();

    this.saving.set(true);
    this.form.get('email')?.setErrors(null); // limpia 'duplicate'

    const { uid, nombre, email, telefono, direccion } = this.form.getRawValue();
    const base = (this.data?.cliente ?? {}) as Cliente;

    if (this.isEdit()) {
      const patch: ClientePatch = {};
      if (nombre !== base.nombre) patch.nombre = nombre!;
      if (email !== base.email)   patch.email = email!;
      if (telefono !== base.telefono) patch.telefono = (telefono || '') as string;
      if (direccion !== base.direccion) patch.direccion = (direccion || '') as string;

      if (Object.keys(patch).length === 0) { this.saving.set(false); this.ref.close(false); return; }

      this.svc.update(uid!, patch).subscribe({
        next: () => { this.saving.set(false); this.snack.open('Cliente actualizado', 'OK', { duration: 1800 }); this.ref.close(true); },
        error: (err) => this.handleError(err)
      });
    } else {
      const body: ClienteCreate = {
        nombre: nombre!,
        email: email!,
        telefono: (telefono || null) as any,
        direccion: (direccion || null) as any
      };
      this.svc.create(body).subscribe({
        next: () => { this.saving.set(false); this.snack.open('Cliente creado', 'OK', { duration: 1800 }); this.ref.close(true); },
        error: (err) => this.handleError(err)
      });
    }
  }

  private handleError(err: any) {
    this.saving.set(false);
    const msg: string = err?.error?.message || err?.message || '';
    if (msg.toLowerCase().includes('email') && msg.toLowerCase().includes('registrado')) {
      this.form.get('email')?.setErrors({ duplicate: true });
      this.snack.open('El email ya está registrado.', 'Cerrar', { duration: 3000 });
      return;
    }
    this.snack.open(msg || 'No se pudo guardar. Intenta de nuevo.', 'Cerrar', { duration: 3500 });
  }

  close() {
    if (this.saving()) return;
    this.ref.close(false);
  }
}
