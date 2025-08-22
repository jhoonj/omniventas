import { Routes } from '@angular/router';
import { InicioComponent } from './pages/inicio/inicio';
import { ProductosComponent } from './pages/productos/productos.component';
import { FormulasComponent } from './pages/formulas/formulas';
import { PedidosComponent } from './pages/pedidos/pedidos';
import { EnviosComponent } from './pages/envios/envios';
import { ClientesComponent } from './pages/clientes/clientes.component';
import { UsuariosComponent } from './pages/usuarios/usuarios.component';
import { ProveedoresComponent } from './pages/proveedores/proveedores.component';
import { MiniCheckoutComponent } from './pages/carrito/mini-checkout.component';
import { LoginComponent } from './pages/login/login';
import { CatalogoComponent } from './pages/catalogo/catalogo.component';
import { ProductoDetalleComponent } from './pages/producto-detalle/producto-detalle.component';
import { AuthGuard } from './core/auth/auth.guard';
import { L } from '@angular/cdk/keycodes';

export const routes: Routes = [


    { path: '', redirectTo: 'catalogo', pathMatch: 'full' },

  { path: 'catalogo', component: CatalogoComponent },
  { path: 'login', component: LoginComponent },
 { path: 'Carrito', component: MiniCheckoutComponent },
  { path: 'producto/:uid', component: ProductoDetalleComponent },


  // Protegidas (requieren login)
  { path: 'inicio', component: InicioComponent, canActivate: [AuthGuard] },
  { path: 'productos', component: ProductosComponent, canActivate: [AuthGuard] },
  { path: 'formulas', component: FormulasComponent, canActivate: [AuthGuard] },
  { path: 'pedidos', component: PedidosComponent, canActivate: [AuthGuard] },
  { path: 'envios', component: EnviosComponent, canActivate: [AuthGuard] },
  { path: 'clientes', component: ClientesComponent, canActivate: [AuthGuard] },
  { path: 'usuarios', component: UsuariosComponent, canActivate: [AuthGuard] },
  { path: 'proveedores', component: ProveedoresComponent, canActivate: [AuthGuard] },


  
  { path: '**', redirectTo: 'catalogo' },


];
