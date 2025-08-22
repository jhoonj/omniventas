export interface Producto {
  uid: string;
  nombre: string;
  descripcion: string;
  tipo: string;
  precio: number;
  proveedorId: number;
  updatedAt: string; // ISO
  stock: number;
  imageUrl?: string | null; // URL para mostrar en el catálogo
}

export interface ImagenProducto {
  uid: string;
  filename: string;
  contentType: string;
  sizeBytes: number;
  principal: boolean;
  altText: string;
  url: string; // puede venir absoluto o relativo
  createdAt: string; // ISO
}

export interface ApiEnvelope<T> {
  success: boolean;
  data: T;
  error?: any;
}
