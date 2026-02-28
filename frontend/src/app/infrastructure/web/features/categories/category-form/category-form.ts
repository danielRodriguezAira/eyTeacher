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
    private cdr = inject(ChangeDetectorRef);

    ngOnInit() {
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
            const idParam = this.route.snapshot.paramMap.get('id');
            const category = this.categoryForm.value as Category;
            category.id = idParam ? Number(idParam) : null;
            this.categoryService.saveCategory(category).subscribe({
                next: (created: any) => {
                    this.notificationService.openSnackBar('Categoría guardada correctamente');
                    if (created?.id) {
                        this.router.navigate(['/category-detail', created.id]);
                    } else {
                        this.router.navigate(['/category-list']);
                    }
                },
                error: (error) => {
                    this.notificationService.openSnackBar(error.error);
                }
            });
        }
    }

    onCancel() {
        this.router.navigate(['/category-list']);
    }
}
