import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-categories',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatListModule, MatIconModule],
  templateUrl: './categories.component.html',
  styleUrl: './categories.component.css'
})
export class CategoriesComponent {
  categories = [
    { name: 'Matemáticas', icon: 'functions', description: 'Cálculo, Álgebra y Geometría' },
    { name: 'Lengua', icon: 'menu_book', description: 'Gramática y Literatura' },
    { name: 'Ciencias', icon: 'science', description: 'Biología, Física y Química' },
    { name: 'Historia', icon: 'history', description: 'Historia Universal y Geografía' },
    { name: 'Inglés', icon: 'language', description: 'Grammar and Vocabulary' },
    { name: 'Arte', icon: 'palette', description: 'Dibujo y Pintura' }
  ];
}
