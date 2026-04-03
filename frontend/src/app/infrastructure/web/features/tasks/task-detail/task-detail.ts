import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatDialog} from '@angular/material/dialog';
import {map} from 'rxjs';
import {Task} from '../../../../../domain/entities/task';
import {Solution} from '../../../../../domain/entities/solution';
import {TaskService} from '../../../services/task.service';
import {SolutionService} from '../../../services/solution.service';
import {NotificationService} from '../../../services/notification.service';
import {UserRole} from "../../../../../domain/entities/auth-user";
import {AuthenticationService} from '../../../services/auth.service';
import {toSignal} from '@angular/core/rxjs-interop';
import {SafeHtmlPipe} from '../../../../../shared/pipes/safe-html.pipe';
import {ConfirmDialog} from '../../../../../shared/components/confirm-dialog/confirm-dialog';

const PAGE_SIZE = 10;

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
    private solutionService = inject(SolutionService);
    private router = inject(Router);
    private notificationService = inject(NotificationService);
    private authService = inject(AuthenticationService);
    private dialog = inject(MatDialog);

    task = signal<Task | undefined>(undefined);
    solutions = signal<Solution[]>([]);
    hasMoreSolutions = signal(false);
    private currentPage = 0;
    private taskId = 0;

    userRole = toSignal(this.authService.getCurrentUserObservable().pipe(
        map(user => user?.role ?? null)
    ), {initialValue: null});

    UserRole = UserRole;
    user = toSignal(this.authService.getCurrentUserObservable(), {initialValue: null});

    ngOnInit(): void {
        this.taskId = Number(this.route.snapshot.paramMap.get('id'));
        if (this.taskId) {
            this.taskService.getTaskById(this.taskId).subscribe(t => this.task.set(t));
            this.loadSolutions();
        }
    }

    loadSolutions(): void {
        this.solutionService.getSolutionsByTask(this.taskId, this.currentPage, PAGE_SIZE).subscribe(page => {
            this.solutions.update(existing => [...existing, ...page.content]);
            this.hasMoreSolutions.set(page.hasNext);
            this.currentPage++;
        });
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

    canAddSolution(): boolean {
        if (this.userRole() !== UserRole.STUDENT) {
            return false;
        }
        const mySolutions = this.solutions().filter(s => s.student.id === this.user()?.id);
        if (mySolutions.length === 0) {
            return true;
        }
        const latestSolution = mySolutions[mySolutions.length - 1];
        return !!latestSolution.correction;
    }

    deleteTask(task: Task) {
        const ref = this.dialog.open(ConfirmDialog, {
            data: {
                title: 'Borrar tarea',
                message: `¿Seguro que quieres borrar la tarea #${task.id}?`
            }
        });
        ref.afterClosed().subscribe(confirmed => {
            if (confirmed) {
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
        });
    }
}
