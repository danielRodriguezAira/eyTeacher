import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {map} from 'rxjs';
import {Task} from '../../../../../domain/entities/task';
import {TaskService} from '../../../services/task.service';
import {NotificationService} from '../../../services/notification.service';
import {UserRole} from "../../../../../domain/entities/auth-user";
import {AuthenticationService} from '../../../services/auth.service';
import {Solution} from '../../../../../domain/entities/solution';
import {toSignal} from '@angular/core/rxjs-interop';
import {SafeHtmlPipe} from '../../../../../shared/pipes/safe-html.pipe';

@Component({
    selector: 'app-task-detail',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterLink, MatMenu, MatMenuItem, MatMenuTrigger, SafeHtmlPipe],
    templateUrl: './task-detail.html',
    styleUrl: './task-detail.scss'
})
export class TaskDetail implements OnInit {
    private route = inject(ActivatedRoute);
    private taskService = inject(TaskService);
    private router = inject(Router);
    private notificationService = inject(NotificationService);
    private authService = inject(AuthenticationService);

    task = signal<Task | undefined>(undefined);
    userRole = toSignal(this.authService.getCurrentUserObservable().pipe(
        map(user => user?.role ?? null)
    ), {initialValue: null});

    UserRole = UserRole;
    user = toSignal(this.authService.getCurrentUserObservable(), {initialValue: null});

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        if (id) {
            this.taskService.getTaskById(id).subscribe(t => this.task.set(t));
        }
    }

    viewSolution(taskId: number, solution: Solution) {
        this.router.navigate(['/task', taskId, 'solution', solution.id]);
    }

    editTask(task: Task) {
        this.router.navigate(['/task-edit', task.id]);
    }

    addSolution(task: Task) {
        this.router.navigate(['/task', task.id, 'solution-form']);
    }

    canAddSolution(task: Task): boolean {
        if (this.userRole() !== UserRole.STUDENT) {
            return false;
        }

        if (!task.solutionList || task.solutionList.length === 0) {
            return true;
        }

        const mySolutions = task.solutionList.filter(s => s.student.id === this.user()?.id);
        if (mySolutions.length === 0) {
            return true;
        }

        const latestSolution = mySolutions[mySolutions.length - 1];
        return !!latestSolution.correction;
    }

    deleteTask(task: Task) {
        if (confirm(`¿Seguro que quieres borrar la tarea #${task.id}?`)) {
            this.taskService.deleteTask(task.id!).subscribe({
                next: () => {
                    this.notificationService.openSnackBar('Tarea borrada correctamente');
                    this.router.navigate(['/topic-detail', task.topicId]);
                },
                error: (err) => {
                    this.notificationService.openSnackBar('Error al borrar la tarea');
                    console.error(err);
                }
            });
        }
    }
}
