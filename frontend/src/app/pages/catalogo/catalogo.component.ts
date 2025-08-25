// src/app/pages/catalogo/catalogo.component.ts
import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider'; // 👈 AÑADE ESTA LÍNEA
import { Subscription } from 'rxjs';
import { startWith, debounceTime } from 'rxjs/operators';
import { toSignal } from '@angular/core/rxjs-interop';
import { CatalogoService } from './catalogo.service';
import { Producto, ImagenProducto } from './models';
import { CartService } from '../carrito/cart.service';
import { MiniCheckoutComponent } from '../carrito/mini-checkout.component';

import { RouterLink } from '@angular/router';

type SortKey = 'recientes' | 'precio-asc' | 'precio-desc' | 'relevancia';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [
    CommonModule, ReactiveFormsModule, RouterModule,
    MatFormFieldModule, MatInputModule, MatSelectModule,
    MatIconModule, MatButtonModule, MatButtonToggleModule, MatChipsModule,
    MatPaginatorModule, MatProgressSpinnerModule, MatSnackBarModule,
    MatDividerModule, // 👈 AÑADE EL MÓDULO AQUÍ
    CurrencyPipe, MiniCheckoutComponent
  ],
  templateUrl: './catalogo.html',
  styleUrls: ['./catalogo.scss']
})
export class CatalogoComponent implements OnInit, OnDestroy {
  private fb = inject(FormBuilder);
  private svc = inject(CatalogoService);
  private router = inject(Router);
  private snack = inject(MatSnackBar);
  cart = inject(CartService);
  showFilters = false;
  goToUserMenu(){ this.router.navigateByUrl('/login'); }

  cargando = signal<boolean>(true);
  error = signal<string | null>(null);
  productos = signal<Producto[]>([]);

  tipos = computed(() => {
    const set = new Set<string>();
    for (const p of this.productos()) if (p?.tipo) set.add(p.tipo);
    return Array.from(set).sort((a,b)=>a.localeCompare(b));
  });

  private imgCacheSig = signal<Record<string, string>>({});
  private requestedImgs = new Set<string>();
  private missingImgs = new Set<string>();
  img(uid: string): string | undefined { return this.imgCacheSig()[uid]; }
  private setImg(uid: string, url: string) { this.imgCacheSig.set({ ...this.imgCacheSig(), [uid]: url }); }

  form = this.fb.nonNullable.group({
    q: '',
    tipo: 'todos',
    sort: 'recientes' as SortKey,
    precioMin: this.fb.nonNullable.control<number | null>(null),
    precioMax: this.fb.nonNullable.control<number | null>(null),
  });
  private form$ = this.form.valueChanges.pipe(startWith(this.form.getRawValue()), debounceTime(50));
  formSig = toSignal(this.form$, { initialValue: this.form.getRawValue() });

  sortLabels: Record<SortKey,string> = {
    'recientes': 'Más recientes','precio-asc': 'Precio ↑','precio-desc': 'Precio ↓','relevancia': 'Relevancia'
  };

  chips = computed(() => {
    const v = this.formSig();
    const cs: {key:string,label:string}[] = [];
    if (v.q?.trim()) cs.push({ key: 'q', label: `Búsqueda: "${v.q.trim()}"` });
    if (v.tipo && v.tipo !== 'todos') cs.push({ key: 'tipo', label: `Tipo: ${v.tipo}` });
    if (v.precioMin != null) cs.push({ key: 'precioMin', label: `≥ ${v.precioMin.toLocaleString()}` });
    if (v.precioMax != null) cs.push({ key: 'precioMax', label: `≤ ${v.precioMax.toLocaleString()}` });
    if (v.sort && v.sort !== 'recientes') cs.push({ key: 'sort', label: this.sortLabels[v.sort] });
    return cs;
  });

  pageIndex = signal(0);
  pageSize = signal(12);
  get length() { return this.filtered().length; }

  private subs: Subscription[] = [];

  ngOnInit(): void {
    const s = this.svc.listarProductos().subscribe({
      next: (items) => { this.productos.set(items || []); this.cargando.set(false); setTimeout(() => this.prefetchImagesForCurrentPage(), 0); },
      error: (e) => { console.error(e); this.error.set('No se pudo cargar el catálogo.'); this.cargando.set(false); }
    });
    this.subs.push(s);
  }
  ngOnDestroy(): void { this.subs.forEach(s => s.unsubscribe()); }

  filtered = computed(() => {
    const { q, tipo, sort, precioMin, precioMax } = this.formSig();
    let items = [...this.productos()];
    const qn = (q || '').trim().toLowerCase();
    if (qn) items = items.filter(p => (p.nombre || '').toLowerCase().includes(qn) || (p.descripcion || '').toLowerCase().includes(qn));
    if (tipo && tipo !== 'todos') items = items.filter(p => (p.tipo || '').toLowerCase() === tipo.toLowerCase());
    if (precioMin != null) items = items.filter(p => (p.precio ?? 0) >= precioMin);
    if (precioMax != null) items = items.filter(p => (p.precio ?? 0) <= precioMax);
    switch (sort) { case 'precio-asc': items.sort((a,b)=>(a.precio??0)-(b.precio??0)); break;
                    case 'precio-desc': items.sort((a,b)=>(b.precio??0)-(a.precio??0)); break;
                    case 'recientes': items.sort((a,b)=> new Date(b.updatedAt).getTime() - new Date(a.updatedAt).getTime()); break; }
    return items;
  });

  pageItems() { const start = this.pageIndex() * this.pageSize(); return this.filtered().slice(start, start + this.pageSize()); }
  onPage(ev: PageEvent) { this.pageIndex.set(ev.pageIndex); this.pageSize.set(ev.pageSize); this.prefetchImagesForCurrentPage(); }

  clearQ(){ this.form.controls.q.setValue(''); this.pageIndex.set(0); }
  allProducts(){ this.form.patchValue({ q:'', tipo:'todos', sort:'recientes', precioMin:null, precioMax:null }); this.pageIndex.set(0); }
  selectTipo(t: string){ this.form.controls.tipo.setValue(t || 'todos'); this.pageIndex.set(0); }
  setSort(s: SortKey){ this.form.controls.sort.setValue(s); this.pageIndex.set(0); }
  clearChip(key: string){ switch (key) { case 'q': this.form.controls.q.setValue(''); break;
    case 'tipo': this.form.controls.tipo.setValue('todos'); break;
    case 'precioMin': this.form.controls.precioMin.setValue(null); break;
    case 'precioMax': this.form.controls.precioMax.setValue(null); break;
    case 'sort': this.form.controls.sort.setValue('recientes'); break; }
    this.pageIndex.set(0);
  }

  private ensureImage(uid: string){
    if (this.img(uid)) return;
    if (this.missingImgs.has(uid)) return;
    if (this.requestedImgs.has(uid)) return;
    this.requestedImgs.add(uid);
    const sub = this.svc.listarImagenes(uid).subscribe({
      next: (imgs: ImagenProducto[]) => {
        this.requestedImgs.delete(uid);
        if (!imgs || !imgs.length){ this.missingImgs.add(uid); return; }
        const principal = imgs.find((img: ImagenProducto)=>img.principal) ?? imgs[0];
        if (principal?.url) this.setImg(uid, this.svc.absApi(principal.url));
      },
      error: () => { this.requestedImgs.delete(uid); this.missingImgs.add(uid); }
    });
    this.subs.push(sub);
  }
  private prefetchImagesForCurrentPage(){ for (const p of this.pageItems()) this.ensureImage(p.uid); }

  // ✅ Simple y seguro con nuestro CartService
  addToCart(p: Producto){
    this.cart.add(p, 1);
    this.cart.open();
    this.snack.open('Agregado al carrito', 'Ver', { duration: 2000 }).onAction().subscribe(() => this.cart.open());
  }
}