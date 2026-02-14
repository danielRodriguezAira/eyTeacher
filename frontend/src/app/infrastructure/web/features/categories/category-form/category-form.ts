import {Component, inject, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {CategoryService} from '../../../../services/category.service';
import {Category} from '../../../../../domain/entities/category';

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

  ngOnInit() {
    this.categoryId = Number(this.route.snapshot.paramMap.get('id'));
    if (this.categoryId) {
      this.isEditMode = true;
      this.titleService.setTitle('Editar Categoría');
      const category = this.categoryService.getCategoryById(this.categoryId);
      if (category) {
        this.categoryForm.patchValue({
          name: category.name,
          description: category.description
        });
      }
    } else {
      this.titleService.setTitle('Nueva Categoría');
    }
  }

  saveCategory() {
    if (this.categoryForm.valid) {
      let category = this.categoryForm.value as Category;
      category.id = Number(this.categoryId);
      console.log('Saving category:', category);
      const savedCategory = this.categoryService.saveCategory(category);
      alert('Categoría guardada (simulado)');
      this.router.navigate(['/category-detail', savedCategory?.id]);
    }
  }

  onCancel() {
    this.router.navigate(['/category-list']);
  }
}
