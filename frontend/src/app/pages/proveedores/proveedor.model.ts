// src/app/pages/proveedores/proveedor.model.ts
export interface Proveedor {
  id?: number;            // ← opcional, solo lectura
  uid: string;            // ← seguimos usando UID para enlazar desde productos
  nombre: string;
  email?: string | null;
  telefono?: string | null;
  direccion?: string | null;
}

export type ProveedorCreate = Omit<Proveedor, 'uid' | 'id'>;
export type ProveedorPatch  = Partial<Omit<Proveedor, 'uid' | 'id'>>;
