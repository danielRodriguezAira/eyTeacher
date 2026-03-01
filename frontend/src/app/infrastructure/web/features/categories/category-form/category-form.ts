import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {CategoryService} from '../../../services/category.service';
import {Category} from '../../../../../domain/entities/category';
import {NotificationService} from '../../../services/notification.service';
import {AuthenticationService} from '../../../services/auth.service';
import {UserRole} from '../../../../../domain/entities/auth-user';

@Component({
    selector: 'app-category-form',
    standalone: true,
    imports: [
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule
    ],
    templateUrl: './category-form.html',
    styleUrls: ['./category-form.css']
})
export class CategoryForm implements OnInit {
    categoryForm = new FormGroup({
        name: new FormControl('', Validators.required),
        description: new FormControl('', Validators.required),
    });

    isEditMode = false;
    private categoryId: number | null = null;
    private titleService = inject(Title);
    private categoryService = inject(CategoryService);
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private notificationService = inject(NotificationService);
    private authService = inject(AuthenticationService);
    private cdr = inject(ChangeDetectorRef);

    ngOnInit() {
        const currentUser = this.authService.getCurrentUser();
        if (currentUser && currentUser.role !== UserRole.TEACHER) {
            this.notificationService.openSnackBar('No tienes permisos para acceder a esta página');
            this.router.navigate(['/category-list']);
            return;
        }

        const idParam = this.route.snapshot.paramMap.get('id');
        this.categoryId = idParam ? Number(idParam) : null;
        if (this.categoryId) {
            this.isEditMode = true;
            this.titleService.setTitle('Editar Categoría');
            this.categoryService.getCategoryById(this.categoryId).subscribe(category => {
                if (category) {
                    this.categoryForm.patchValue({
                        name: category.name,
                        description: category.description
                    });
                    this.cdr.detectChanges();
                }
            });
        } else {
            this.titleService.setTitle('Nueva Categoría');
        }
    }

    saveCategory() {
        if (this.categoryForm.valid) {
            const category = this.categoryForm.value as Category;
            category.id = this.categoryId;
            this.categoryService.saveCategory(category).subscribe({
                next: (saved: any) => {
                    this.notificationService.openSnackBar('Categoría guardada correctamente');
                    const targetId = saved?.id || this.categoryId;
                    if (targetId) {
                        this.router.navigate(['/category-detail', targetId]);
                    } else {
                        this.router.navigate(['/category-list']);
                    }
                },
                error: (error) => {
                    this.notificationService.openSnackBar(error.error || 'Error al guardar la categoría');
                }
            });
        }
    }

    onCancel() {
        if (this.isEditMode && this.categoryId) {
            this.router.navigate(['/category-detail', this.categoryId]);
        } else {
            this.router.navigate(['/category-list']);
        }
    }
}
