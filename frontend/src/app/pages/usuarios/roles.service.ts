import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Rol {
  id: number;
  nombre: string;
}

@Injectable({ providedIn: 'root' })
export class RolesService {
  private baseUrl = 'http://localhost:8080/omniventas/roles';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Rol[]> {
    return this.http.get<Rol[]>(this.baseUrl);
  }
}
