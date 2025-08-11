export interface Producto {
  uid: string;
  nombre: string;
  descripcion?: string | null;
  tipo?: 'lente' | 'armazon' | 'accesorio' | 'otro' | null;
  precio: number | null;
  proveedorUid?: string | null; // opcional
  stock?: number;  

}

export type ProductoCreate = Omit<Producto, 'uid'>;
export type ProductoPatch  = Partial<Omit<Producto, 'uid'>>;
