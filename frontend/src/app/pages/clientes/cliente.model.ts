export interface Cliente {
  id?: number;
  nombre: string;
  email?: string | null;
  telefono?: string | null;
  direccion?: string | null;
}