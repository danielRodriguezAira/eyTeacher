import {Category} from '../../domain/entities/category';
import {Observable} from 'rxjs';

export interface CategoryServicePort {

    getCategories(): Observable<Category[]>;

    getCategoryById(id: number): Observable<Category>;

    saveCategory(category: Category): any;

    deleteCategory(id: number): Observable<void>;
}
