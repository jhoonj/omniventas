export interface Producto {
  uid: string;
  nombre: string;
  descripcion?: string | null;
  tipo?: 'lente' | 'armazon' | 'accesorio' | 'otro' | null;
  precio: number | null;
  proveedorUid?: string | null; // opcional
  stock?: number;  

}

export interface ImagenProducto {
  uid: string;
  filename: string | null;
  contentType: string | null;
  sizeBytes: number;
  principal: boolean;
  altText: string | null;
  url: string;            // en tu backend no puede ser null
  createdAt: string;      // ISO (OffsetDateTime)
}

export type ProductoCreate = Omit<Producto, 'uid'>;
export type ProductoPatch  = Partial<Omit<Producto, 'uid'>>;
