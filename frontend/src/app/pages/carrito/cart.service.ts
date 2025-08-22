// src/app/pages/carrito/cart.service.ts
import { Injectable, computed, signal } from '@angular/core';
import { Producto } from '../catalogo/models';

export type CartItem = { producto: Producto; qty: number; };

@Injectable({ providedIn: 'root' })
export class CartService {
  // Drawer state
  private openedSig = signal<boolean>(false);
  opened() { return this.openedSig(); }
  open() { this.openedSig.set(true); }
  close() { this.openedSig.set(false); }
  toggle() { this.openedSig.set(!this.openedSig()); }

  // Items
  private itemsState = signal<CartItem[]>([]);
  items() { return this.itemsState(); }
  count = computed(() => this.itemsState().reduce((s, it) => s + (it.qty ?? 0), 0));
  total = computed(() => this.itemsState().reduce((s, it) => s + ((it.producto?.precio ?? 0) * (it.qty ?? 0)), 0));

  // Aliases de compatibilidad por si existen componentes antiguos
  countSig() { return this.count(); }
  itemsSig() { return this.items(); }
  totalSig() { return this.total(); }

  // Operaciones
  add(p: Producto, qty: number = 1) {
    const arr = [...this.itemsState()];
    const idx = arr.findIndex(it => it.producto?.uid === p.uid);
    if (idx >= 0) arr[idx] = { ...arr[idx], qty: (arr[idx].qty ?? 0) + (qty ?? 1) };
    else arr.push({ producto: p, qty: qty ?? 1 });
    this.itemsState.set(arr);
  }
  remove(uid: string) { this.itemsState.set(this.itemsState().filter(it => it.producto?.uid !== uid)); }
  update(uid: string, qty: number) {
    if (qty <= 0) return this.remove(uid);
    const arr = [...this.itemsState()];
    const i = arr.findIndex(it => it.producto?.uid === uid);
    if (i >= 0) { arr[i] = { ...arr[i], qty }; this.itemsState.set(arr); }
  }
  clear() { this.itemsState.set([]); }
}