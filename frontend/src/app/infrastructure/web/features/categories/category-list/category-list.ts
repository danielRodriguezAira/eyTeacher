import {Component, inject, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatMenuModule} from '@angular/material/menu';
import {Router, RouterLink} from '@angular/router';
import {Category} from '../../../../../domain/entities/category';
import {CategoryService} from '../../../../services/category.service';

@Component({
  selector: 'app-category-list',
  standalone: true,
  imports: [CommonModule, RouterLink, MatCardModule, MatListModule, MatIconModule, MatButtonModule, MatMenuModule],
  templateUrl: './category-list.html',
  styleUrl: './category-list.css'
})
export class CategoryList implements OnInit {
  categoryList: Category[] = [];

  private categoryService = inject(CategoryService);
  private router = inject(Router);

  ngOnInit(): void {
    this.categoryList = this.categoryService.getCategories();
  }

  viewCategory(category: Category) {
    this.router.navigate(['/category-detail', category.id]);
  }
}
