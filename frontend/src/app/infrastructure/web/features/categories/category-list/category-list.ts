import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatMenuModule} from '@angular/material/menu';
import {Router, RouterLink} from '@angular/router';
import {map, Observable} from 'rxjs';
import {Category} from '../../../../../domain/entities/category';
import {CategoryService} from '../../../services/category.service';
import {AuthenticationService} from '../../../services/auth.service';
import {UserRole} from '../../../../../domain/entities/auth-user';

@Component({
    selector: 'app-category-list',
    standalone: true,
    imports: [CommonModule, RouterLink, MatCardModule, MatListModule, MatIconModule, MatButtonModule, MatMenuModule],
    templateUrl: './category-list.html',
    styleUrl: './category-list.css'
})
export class CategoryList {
    categoryList$: Observable<Category[]>;
    userRole$: Observable<UserRole | null>;
    UserRole = UserRole;

    private categoryService = inject(CategoryService);
    private authService = inject(AuthenticationService);
    private router = inject(Router);

    constructor() {
        this.categoryList$ = this.categoryService.getCategories();
        this.userRole$ = this.authService.getCurrentUserObservable().pipe(
            map(user => user ? user.role : null)
        );
    }

    viewCategory(category: Category) {
        this.router.navigate(['/category-detail', category.id]);
    }
}
