import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, of} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {Category} from '../../../domain/entities/category';
import {CategoryServicePort} from '../../../application/services/category.service.port';
import {AuthenticationService} from './auth.service';
import {UserRole} from "../../../domain/entities/auth-user";

@Injectable({
    providedIn: 'root'
})
export class CategoryService implements CategoryServicePort {
    private http = inject(HttpClient);
    private authService = inject(AuthenticationService);
    private readonly API_URL = '/api/v1/categories';

    // Datos locales simulados mientras no haya listado desde backend
    categoryList: Category[] = [];

    getCategories(): Observable<Category[]> {
        return this.authService.getCurrentUserObservable().pipe(
            switchMap(user => {
                if (user && user.id) {
                    if (user.role === UserRole.STUDENT) {
                        return this.http.get<Category[]>(`${this.API_URL}/student/${user.id}`);
                    }
                    if (user.role === UserRole.TEACHER) {
                        return this.http.get<Category[]>(`${this.API_URL}/owner/${user.id}`);
                    }
                }
                return of([]);
            })
        );
    }

    getCategoryById(id: number): Observable<Category> {
        return this.http.get<Category>(`${this.API_URL}/${id}`);
    }

    saveCategory(category: Category): Observable<any> {
        const currentUser = this.authService.getCurrentUser();
        const ownerId = currentUser?.id;
        const body = {
            id: category.id,
            name: category.name,
            description: category.description,
            ownerId: ownerId
        };
        return this.http.post<any>(`${this.API_URL}`, body);
    }

    deleteCategory(id: number | null): Observable<void> {
        return this.http.delete<void>(`${this.API_URL}/${id}`);
    }
}
