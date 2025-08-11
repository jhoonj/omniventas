export interface Usuario {
  id?: number;
  nombre: string;
  email: string;
  contrasenaHash?: string; // solo se envía al crear o cambiar contraseña
  rol_id?: number;
}
