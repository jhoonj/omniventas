// src/app/shared/ui/cart-toggle-button.component.ts
import { Component, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { CartService } from '../../pages/carrito/cart.service';

@Component({
  selector: 'app-cart-toggle-button',
  standalone: true,
  imports: [CommonModule, MatIconModule, MatButtonModule],
  template: `
    <button mat-icon-button class="rounded-xl border border-slate-200 relative" (click)="open()" aria-label="Abrir carrito">
      <mat-icon>shopping_cart</mat-icon>
      <span *ngIf="count() > 0" class="badge">{{ count() }}</span>
    </button>
  `,
  styles: [`
    :host { display: inline-block; }
    .badge {
      position: absolute; top: 0; right: 0;
      transform: translate(35%, -35%);
      background: #ef4444; color: #fff; border-radius: 999px;
      min-width: 18px; height: 18px; font-size: 11px; line-height: 18px; text-align: center;
      padding: 0 4px; box-shadow: 0 2px 6px rgba(0,0,0,.25);
    }
  `]
})
export class CartToggleButtonComponent {
  private cart = inject(CartService);
  count = computed(() => this.cart.count());
  open(){ this.cart.open(); }
}