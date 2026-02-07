import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'categorias', pathMatch: 'full' },
  {
    path: 'categorias',
    loadComponent: () => import('./categorias/categories.component').then(m => m.CategoriesComponent)
  },
  {
    path: 'alumnos',
    loadComponent: () => import('./alumnos/alumnos.component').then(m => m.AlumnosComponent)
  },
  { path: '**', redirectTo: 'categorias' }
];
