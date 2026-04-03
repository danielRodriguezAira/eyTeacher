import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';
import {MatDialog} from '@angular/material/dialog';
import {Category} from '../../../../../domain/entities/category';
import {CategoryService} from '../../../services/category.service';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {NotificationService} from '../../../services/notification.service';
import {map} from 'rxjs';
import {AuthenticationService} from '../../../services/auth.service';
import {UserRole} from '../../../../../domain/entities/auth-user';
import {toSignal} from '@angular/core/rxjs-interop';
import {SafeHtmlPipe} from '../../../../../shared/pipes/safe-html.pipe';
import {ConfirmDialog} from '../../../../../shared/components/confirm-dialog/confirm-dialog';

@Component({
    selector: 'app-category-detail',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterLink, MatMenu, MatMenuItem, MatMenuTrigger, MatListModule, SafeHtmlPipe],
    templateUrl: './category-detail.html',
    styleUrl: './category-detail.scss'
})
export class CategoryDetail implements OnInit {
    private route = inject(ActivatedRoute);
    private categoryService = inject(CategoryService);
    private authService = inject(AuthenticationService);
    private router = inject(Router);
    private notificationService = inject(NotificationService);
    private dialog = inject(MatDialog);

    category = signal<Category | undefined>(undefined);
    userRole = toSignal(this.authService.getCurrentUserObservable().pipe(
        map(user => user?.role ?? null)
    ), {initialValue: null});

    UserRole = UserRole;

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        if (id) {
            this.categoryService.getCategoryById(id).subscribe(cat => this.category.set(cat));
        }
    }

    editCategory(category: Category) {
        this.router.navigate(['/category-edit', category.id]);
    }

    deleteCategory(category: Category) {
        const ref = this.dialog.open(ConfirmDialog, {
            data: {
                title: 'Borrar categoría',
                message: `¿Seguro que quieres borrar la categoría "${category.name}"?`
            }
        });
        ref.afterClosed().subscribe(confirmed => {
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
        });
    }

    addTopic(categoryId: number | null) {
        this.router.navigate(['/topic-add'], {queryParams: {categoryId}});
    }

    viewTopic(topicId: number | null) {
        if (topicId) {
            this.router.navigate(['/topic-detail', topicId]);
        }
    }
}
