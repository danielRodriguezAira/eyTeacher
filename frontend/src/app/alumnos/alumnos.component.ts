import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatProgressBarModule } from '@angular/material/progress-bar';

@Component({
  selector: 'app-alumnos',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatProgressBarModule],
  templateUrl: './alumnos.component.html',
  styleUrl: './alumnos.component.css'
})
export class AlumnosComponent {
  displayedColumns: string[] = ['nombre', 'email', 'progreso'];
  alumnos = [
    { nombre: 'Juan Pérez', email: 'juan.perez@example.com', progreso: 85 },
    { nombre: 'María García', email: 'maria.garcia@example.com', progreso: 92 },
    { nombre: 'Carlos Rodríguez', email: 'carlos.rod@example.com', progreso: 45 },
    { nombre: 'Ana Martínez', email: 'ana.mtz@example.com', progreso: 78 },
    { nombre: 'Luis Sánchez', email: 'luis.sanchez@example.com', progreso: 60 }
  ];
}
