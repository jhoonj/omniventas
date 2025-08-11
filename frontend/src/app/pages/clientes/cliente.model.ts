// src/app/pages/clientes/cliente.model.ts
export interface Cliente {
  uid: string;               // identificador público (UUID)
  nombre: string;
  email?: string | null;
  telefono?: string | null;
  direccion?: string | null;
}

// Payloads útiles
export type ClienteCreate = Omit<Cliente, 'uid'>;
export type ClientePatch = Partial<Omit<Cliente, 'uid'>>;
