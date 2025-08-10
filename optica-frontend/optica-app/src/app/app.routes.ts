import { Routes } from '@angular/router';
import { InicioComponent } from './pages/inicio/inicio';
import { ProductosComponent } from './pages/productos/productos.component';
import { FormulasComponent } from './pages/formulas/formulas';
import { PedidosComponent } from './pages/pedidos/pedidos';
import { EnviosComponent } from './pages/envios/envios';
import { ClientesComponent } from './pages/clientes/clientes.component';
import { UsuariosComponent } from './pages/usuarios/usuarios.component';
import { ProveedoresComponent } from './pages/proveedores/proveedores.component';
import { LoginComponent } from './pages/login/login';

export const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'inicio', component: InicioComponent },
  { path: 'productos', component: ProductosComponent },
  { path: 'login', component: LoginComponent },
  { path: 'formulas', component: FormulasComponent },
  { path: 'pedidos', component: PedidosComponent },
  { path: 'envios', component: EnviosComponent },
  { path: 'clientes', component: ClientesComponent },
  { path: 'usuarios', component: UsuariosComponent },
  { path: 'proveedores', component: ProveedoresComponent }
];
