import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {map, Observable} from 'rxjs';
import {Task} from '../../../../../domain/entities/task';
import {TaskService} from '../../../services/task.service';
import {NotificationService} from '../../../services/notification.service';
import {UserRole} from "../../../../../domain/entities/auth-user";
import {AuthenticationService} from '../../../services/auth.service';

import {Solution} from '../../../../../domain/entities/solution';

@Component({
    selector: 'app-task-detail',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterLink, MatMenu, MatMenuItem, MatMenuTrigger],
    templateUrl: './task-detail.html',
    styleUrl: './task-detail.css'
})
export class TaskDetail implements OnInit {
    task$?: Observable<Task>;
    userRole$: Observable<UserRole | null>;
    private route = inject(ActivatedRoute);
    private taskService = inject(TaskService);
    private router = inject(Router);
    private notificationService = inject(NotificationService);
    private authService = inject(AuthenticationService);

    constructor() {
        this.userRole$ = this.authService.getCurrentUserObservable().pipe(
            map(user => user ? user.role : null)
        );
    }

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        if (id) {
            this.task$ = this.taskService.getTaskById(id);
        }
    }

    viewSolution(taskId: number, solution: Solution) {
        this.router.navigate(['/task', taskId, 'solution', solution.id]);
    }

    editTask(task: Task) {
        this.router.navigate(['/task-edit', task.id]);
    }

    deleteTask(task: Task) {
        const confirmed = confirm(`¿Seguro que quieres borrar la tarea #${task.id}?`);
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
    }

    protected readonly UserRole = UserRole;
}
