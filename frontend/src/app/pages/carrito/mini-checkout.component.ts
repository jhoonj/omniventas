// src/app/pages/carrito/mini-checkout.component.ts
import { Component, inject, computed } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { CartService } from './cart.service';
import { Producto } from '../catalogo/models';

@Component({
  selector: 'app-mini-checkout',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule, CurrencyPipe],
  templateUrl: './mini-checkout.html',
  styleUrls: ['./mini-checkout.scss']
})
export class MiniCheckoutComponent {
  cart = inject(CartService);

  items = computed(() => this.cart.items());
  total = computed(() => this.cart.total());

  placeholder = 'assets/placeholder-product.svg';
  resolveImg(p: Producto | null | undefined): string {
    const anyp: any = p as any;
    return anyp?.imagen || anyp?.imagenUrl || anyp?.url || this.placeholder;
  }

  close() { this.cart.close(); }
  remove(uid: string) { this.cart.remove(uid); }
  dec(uid: string, qty: number) { this.cart.update(uid, Math.max(1, qty - 1)); }
  inc(uid: string, qty: number) { this.cart.update(uid, qty + 1); }
  clear() { this.cart.clear(); }
}