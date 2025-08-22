import { Component, OnDestroy, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { MatDividerModule } from '@angular/material/divider';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule } from '@angular/forms';
import { CurrencyPipe, DatePipe } from '@angular/common';
import { CatalogoService } from '../catalogo/catalogo.service';
import { ImagenProducto, Producto } from '../catalogo/models';
import { forkJoin, Subscription } from 'rxjs';
import { CartService } from '../carrito/cart.service';

@Component({
  selector: 'app-producto-detalle',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatDividerModule,
    MatSnackBarModule,
    FormsModule,
    CurrencyPipe,
    DatePipe
  ],
  templateUrl: './producto-detalle.html',
  styleUrls: ['./producto-detalle.scss'],
})
export class ProductoDetalleComponent implements OnInit, OnDestroy {
  private route = inject(ActivatedRoute);
  private svc = inject(CatalogoService);
  private snack = inject(MatSnackBar); // Type fix if ts complains
  private cart = inject(CartService);

  loading = signal<boolean>(true);
  error = signal<string | null>(null);

  producto = signal<Producto | null>(null);
  imagenes = signal<ImagenProducto[]>([]);
  selectedUrl = signal<string>('');
  qty = signal<number>(1);

  private subs: Subscription[] = [];

  ngOnInit(): void {
    const sub = this.route.paramMap.subscribe((pm) => {
      const uid = pm.get('uid')!;
      if (!uid) return;
      this.cargar(uid);
    });
    this.subs.push(sub);
  }

  ngOnDestroy(): void {
    this.subs.forEach(s => s.unsubscribe());
  }

  private cargar(uid: string) {
    this.loading.set(true);
    this.error.set(null);

    forkJoin({
      prod: this.svc.obtenerProducto(uid),
      imgs: this.svc.listarImagenesDeProducto(uid)
    }).subscribe({
      next: ({ prod, imgs }) => {
        this.producto.set(prod);
        this.imagenes.set(imgs || []);
        // Seleccionar imagen principal
        const principal = this.imagenes().find(i => i.principal) ?? this.imagenes()[0];
        if (principal) {
          const raw = principal.url && /^(http|https):\/\//i.test(principal.url)
            ? principal.url
            : `${(this.svc as any).api}/api/productos/${uid}/imagenes/${principal.uid}/raw`;
          this.selectedUrl.set(raw);
        } else {
          this.selectedUrl.set('');
        }
        this.loading.set(false);
      },
      error: (e) => {
        console.error(e);
        this.error.set('No se pudo cargar el producto.');
        this.loading.set(false);
      }
    });
  }

  select(img: ImagenProducto) {
    if (!img) return;
    const url = img.url && /^(http|https):\/\//i.test(img.url)
      ? img.url
      : `${(this.svc as any).api}/api/productos/${this.producto()!.uid}/imagenes/${img.uid}/raw`;
    this.selectedUrl.set(url);
  }

  inc() { this.qty.set(Math.min(99, this.qty() + 1)); }
  dec() { this.qty.set(Math.max(1, this.qty() - 1)); }

addToCart() {
    const p = this.producto();
    if (!p) return;

    // Adjuntar imagen seleccionada al producto antes de añadir
    p.imageUrl = this.selectedUrl();
    this.cart.add(p, this.qty());

    this.snack
      .open('Agregado al carrito', 'Ver', { duration: 2000 })
      .onAction()
      .subscribe(() => this.cart.open());
  } //  👈 esta llave faltaba

  hideImg(imgEl: HTMLImageElement) {
    if (imgEl) { imgEl.style.display = 'none'; }
  }
}