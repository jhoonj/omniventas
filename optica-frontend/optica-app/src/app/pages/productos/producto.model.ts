export interface Producto {
  id?: number;
  nombre: string;
  descripcion?: string;
  tipo?: string;           // hasta 10 chars
  precio?: number;         // numeric(10,2)
  stock?: number;          // int
  proveedor_id?: number;   // FK -> proveedores.id
}


