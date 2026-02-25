import {Injectable} from '@angular/core';
import {Category} from '../../../domain/entities/category';
import {CategoryServicePort} from '../../../application/services/category.service.port';

@Injectable({
  providedIn: 'root'
})
export class CategoryService implements CategoryServicePort {
  categoryList: Category[] = [
    new Category(1, 'Matemáticas', 'Cálculo, Álgebra y Geometría'),
    new Category(2, 'Lengua', 'Gramática y Literatura'),
    new Category(3, 'Ciencias', 'Biología, Física y Química'),
    new Category(4, 'Historia', 'Historia Universal y Geografía'),
    new Category(5, 'Inglés', 'Grammar and Vocabulary'),
    new Category(6, 'Arte', 'Dibujo y Pintura')
  ];

  getCategories(): Category[] {
    return this.categoryList;
  }

  getCategoryById(id: number): Category | undefined {
    return this.categoryList.find(c => c.id === id);
  }

  saveCategory(category: Category) {
    //TODO: llamada al servicio de categorías: guardar
    return this.getCategoryById(category.id);
  }

  deleteCategory(id: number) {
    //TODO: llamada al servicio de categorías: borrar
  }
}
