import {Category} from '../../domain/entities/category';

export interface CategoryServicePort {
  categoryList: Category[];

  getCategories(): Category[];

  getCategoryById(id: number): Category | undefined;

  saveCategory(category: Category): any;

  deleteCategory(id: number): void;
}
