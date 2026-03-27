import {Component, inject} from '@angular/core';
import {CommonModule} from '@angular/common';
import {of, switchMap} from 'rxjs';
import {toSignal} from '@angular/core/rxjs-interop';
import {Router} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatTableModule} from '@angular/material/table';
import {MatIconModule} from '@angular/material/icon';
import {StudentService} from '../../../services/student.service';
import {AuthenticationService} from '../../../services/auth.service';
import {Student} from '../../../../../domain/entities/student';

@Component({
    selector: 'app-student-list',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatTableModule, MatIconModule],
    templateUrl: './student-list.html',
    styleUrl: './student-list.scss'
})
export class StudentList {
    private readonly studentService = inject(StudentService);
    private readonly authService = inject(AuthenticationService);
    private readonly router = inject(Router);

    displayedColumns = ['name', 'email'];

    students = toSignal(
        this.authService.getCurrentUserObservable().pipe(
            switchMap(user => user ? this.studentService.getStudentsByOwner(user.id) : of([] as Student[]))
        ),
        {initialValue: [] as Student[]}
    );

    viewStudentTasks(student: Student): void {
        this.router.navigate(['/student-tasks', student.id], {
            state: {studentName: `${student.firstName} ${student.lastName}`}
        });
    }
}
