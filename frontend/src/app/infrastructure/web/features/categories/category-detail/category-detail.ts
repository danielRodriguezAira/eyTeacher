import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {Category} from '../../../../../domain/entities/category';
import {CategoryService} from '../../../services/category.service';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {NotificationService} from '../../../services/notification.service';
import {Observable} from 'rxjs';

@Component({
    selector: 'app-category-detail',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterLink, MatMenu, MatMenuItem, MatMenuTrigger],
    templateUrl: './category-detail.html',
    styleUrl: './category-detail.css'
})
export class CategoryDetail implements OnInit {
    category$?: Observable<Category>;
    private route = inject(ActivatedRoute);
    private categoryService = inject(CategoryService);
    private router = inject(Router);
    private notificationService = inject(NotificationService);

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        if (id) {
            this.category$ = this.categoryService.getCategoryById(id);
        }
    }

    editCategory(category: Category) {
        this.router.navigate(['/category-edit', category.id]);
    }

    deleteCategory(category: Category) {
        const confirmed = confirm(`¿Seguro que quieres borrar la categoría "${category.name}"?`);
        if (confirmed) {
            this.categoryService.deleteCategory(category.id).subscribe({
                next: () => {
                    this.notificationService.openSnackBar('Categoría borrada correctamente');
                    this.router.navigate(['/category-list']);
                },
                error: (err) => {
                    this.notificationService.openSnackBar('Error al borrar la categoría');
                    console.error(err);
                }
            });
        }
    }
}
