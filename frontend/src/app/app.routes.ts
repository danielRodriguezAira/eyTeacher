import {Routes} from '@angular/router';
import {authGuard} from './infrastructure/web/guards/auth.guard';

export const routes: Routes = [
    {path: '', redirectTo: 'category-list', pathMatch: 'full'},
    {
        path: 'login',
        loadComponent: () =>
            import('./infrastructure/web/features/auth/login/login').then(m => m.Login)
    },
    {
        path: 'category-list',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./infrastructure/web/features/categories/category-list/category-list').then(m => m.CategoryList)
    },
    {
        path: 'category-add',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./infrastructure/web/features/categories/category-form/category-form').then(m => m.CategoryForm)
    },
    {
        path: 'category-edit/:id',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./infrastructure/web/features/categories/category-form/category-form').then(m => m.CategoryForm)
    },
    {
        path: 'category-detail/:id',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./infrastructure/web/features/categories/category-detail/category-detail').then(m => m.CategoryDetail)
    },
    {
        path: 'student-list',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./infrastructure/web/features/students/student-list/student-list').then(m => m.StudentList)
    },
    {
        path: 'account',
        canActivate: [authGuard],
        loadComponent: () =>
            import('./infrastructure/web/features/account-page/account-page').then(m => m.AccountPage)
    },
    {
        path: 'register',
        loadComponent: () =>
            import('./infrastructure/web/features/account-page/account-page').then(m => m.AccountPage)
    },
    {path: '**', redirectTo: 'category-list'}
];
