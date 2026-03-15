import {ChangeDetectorRef, Component, inject, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {TaskService} from '../../../services/task.service';
import {Task} from '../../../../../domain/entities/task';
import {NotificationService} from '../../../services/notification.service';
import {AuthenticationService} from '../../../services/auth.service';
import {UserRole} from '../../../../../domain/entities/auth-user';

@Component({
    selector: 'app-task-form',
    standalone: true,
    imports: [
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule
    ],
    templateUrl: './task-form.html',
    styleUrls: ['./task-form.css']
})
export class TaskForm implements OnInit {
    taskForm = new FormGroup({
        description: new FormControl('', Validators.required),
    });

    isEditMode = false;
    private taskId: number | null = null;
    private topicId: number | null = null;
    private titleService = inject(Title);
    private taskService = inject(TaskService);
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private notificationService = inject(NotificationService);
    private authService = inject(AuthenticationService);
    private cdr = inject(ChangeDetectorRef);

    ngOnInit() {
        const currentUser = this.authService.getCurrentUser();
        if (currentUser && currentUser.role !== UserRole.TEACHER) {
            this.notificationService.openSnackBar('No tienes permisos para acceder a esta página');
            this.router.navigate(['/category-list']);
            return;
        }

        const idParam = this.route.snapshot.paramMap.get('id');
        const topicIdParam = this.route.snapshot.queryParamMap.get('topicId');
        
        this.taskId = idParam ? Number(idParam) : null;
        this.topicId = topicIdParam ? Number(topicIdParam) : null;

        if (this.taskId) {
            this.isEditMode = true;
            this.titleService.setTitle('Editar Tarea');
            this.taskService.getTaskById(this.taskId).subscribe(task => {
                if (task) {
                    this.taskForm.patchValue({
                        description: task.description
                    });
                    this.topicId = task.topicId;
                    this.cdr.detectChanges();
                }
            });
        } else {
            this.titleService.setTitle('Nueva Tarea');
        }
    }

    saveTask() {
        if (this.taskForm.valid) {
            const taskData = this.taskForm.value;
            const task = new Task(
                this.taskId,
                taskData.description!,
                this.topicId!
            );

            this.taskService.saveTask(task).subscribe({
                next: () => {
                    this.notificationService.openSnackBar('Tarea guardada correctamente');
                    this.router.navigate(['/topic-detail', this.topicId]);
                },
                error: (error) => {
                    this.notificationService.openSnackBar(error.error || 'Error al guardar la tarea');
                }
            });
        }
    }

    onCancel() {
        if (this.topicId) {
            this.router.navigate(['/topic-detail', this.topicId]);
        } else {
            this.router.navigate(['/category-list']);
        }
    }
}
