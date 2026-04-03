import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatMenuModule} from '@angular/material/menu';
import {Router, RouterLink} from '@angular/router';
import {map} from 'rxjs';
import {Category} from '../../../../../domain/entities/category';
import {CategoryService} from '../../../services/category.service';
import {AuthenticationService} from '../../../services/auth.service';
import {UserRole} from '../../../../../domain/entities/auth-user';
import {toSignal} from '@angular/core/rxjs-interop';
import {SafeHtmlPipe} from "../../../../../shared/pipes/safe-html.pipe";

@Component({
    selector: 'app-category-list',
    standalone: true,
    imports: [CommonModule, RouterLink, MatCardModule, MatListModule, MatIconModule, MatButtonModule, MatMenuModule, SafeHtmlPipe],
    templateUrl: './category-list.html',
    styleUrl: './category-list.scss'
})
export class CategoryList {
    private categoryService = inject(CategoryService);
    private authService = inject(AuthenticationService);
    private router = inject(Router);

    categories = toSignal(this.categoryService.getCategories(), {initialValue: []});
    userRole = toSignal(this.authService.getCurrentUserObservable().pipe(
        map(user => user?.role ?? null)
    ), {initialValue: null});

    UserRole = UserRole;

    viewCategory(category: Category) {
        this.router.navigate(['/category-detail', category.id]);
    }
}
