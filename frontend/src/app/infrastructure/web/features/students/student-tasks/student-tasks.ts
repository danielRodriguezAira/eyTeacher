import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';
import {map} from 'rxjs';
import {toSignal} from '@angular/core/rxjs-interop';
import {TaskService} from '../../../services/task.service';
import {AuthenticationService} from '../../../services/auth.service';
import {StudentTasksResponse, TaskStatus} from '../../../../../domain/entities/student-tasks-response';
import {UserRole} from "../../../../../domain/entities/auth-user";

@Component({
    selector: 'app-student-tasks',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, MatListModule],
    templateUrl: './student-tasks.html',
    styleUrl: './student-tasks.scss'
})
export class StudentTasks implements OnInit {
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private taskService = inject(TaskService);
    private authService = inject(AuthenticationService);

    private currentUser = toSignal(this.authService.getCurrentUserObservable().pipe(
        map(user => user ?? null)
    ), {initialValue: null});

    userRole = toSignal(this.authService.getCurrentUserObservable().pipe(
        map(user => user?.role ?? null)
    ), {initialValue: null});

    get studentName(): string {
        if (history.state?.studentName) return history.state.studentName;
        const user = this.currentUser();
        return user ? `${user.firstName} ${user.lastName}` : 'Alumno';
    }

    studentId = '';
    taskGroups = signal<StudentTasksResponse[]>([]);

    readonly statusConfig: Record<TaskStatus, { label: string; icon: string; cssClass: string }> = {
        WITHOUT_SOLUTION: {label: 'Sin solución', icon: 'pending_actions', cssClass: 'status-pending'},
        WITHOUT_CORRECTION: {label: 'Sin corregir', icon: 'rate_review', cssClass: 'status-review'},
        CORRECTED: {label: 'Corregidas', icon: 'check_circle', cssClass: 'status-done'},
    };

    ngOnInit(): void {
        this.studentId = this.route.snapshot.paramMap.get('studentId') ?? '';
        if (this.studentId) {
            this.taskService.getStudentTasks(this.studentId).subscribe(groups => this.taskGroups.set(groups));
        }
    }

    goBack(): void {
        this.router.navigate(['/student-list']);
    }

    viewTask(taskId: number): void {
        this.router.navigate(['/task-detail', taskId]);
    }

    viewSolution(taskId: number, solutionId: number): void {
        this.router.navigate(['/task', taskId, 'solution', solutionId]);
    }

    getStatusConfig(status: TaskStatus) {
        return this.statusConfig[status];
    }

    countTasks(group: StudentTasksResponse): number {
        return group.categories.reduce(
            (total, cat) => total + cat.topics.reduce((t, topic) => t + topic.tasks.length, 0),
            0
        );
    }

    protected readonly UserRole = UserRole;
}
