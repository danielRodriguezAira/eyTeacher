import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Router} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatTableModule} from '@angular/material/table';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {StudentService} from '../../../services/student.service';
import {AuthenticationService} from '../../../services/auth.service';
import {Student} from '../../../../../domain/entities/student';
import {NotificationService} from '../../../services/notification.service';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-student-list',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatTableModule, MatIconModule, MatButtonModule],
    templateUrl: './student-list.html',
    styleUrl: './student-list.scss'
})
export class StudentList implements OnInit {
    private readonly studentService = inject(StudentService);
    private readonly authService = inject(AuthenticationService);
    private readonly router = inject(Router);
    private readonly notificationService = inject(NotificationService);

    displayedColumns = ['name', 'email'];
    students = signal<Student[]>([]);
    hasMore = signal(false);
    private currentPage = 0;
    private ownerId = '';

    ngOnInit(): void {
        const user = this.authService.getCurrentUser();
        if (user?.id) {
            this.ownerId = user.id;
            this.loadStudents();
        }
    }

    loadStudents(): void {
        this.studentService.getStudentsByOwner(this.ownerId, this.currentPage, PAGE_SIZE).subscribe({
            next: (page) => {
                this.students.update(existing => [...existing, ...page.content]);
                this.hasMore.set(page.hasNext);
                this.currentPage++;
            },
            error: () => this.notificationService.openSnackBar('Error al cargar los alumnos')
        });
    }

    viewStudentTasks(student: Student): void {
        this.router.navigate(['/student-tasks', student.id], {
            state: {studentName: `${student.firstName} ${student.lastName}`}
        });
    }
}
