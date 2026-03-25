import {ChangeDetectorRef, Component, inject, OnInit, signal} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {switchMap} from 'rxjs';
import {TaskService} from '../../../services/task.service';
import {TopicService} from '../../../services/topic.service';
import {CategoryService} from '../../../services/category.service';
import {Task} from '../../../../../domain/entities/task';
import {NotificationService} from '../../../services/notification.service';
import {AuthenticationService} from '../../../services/auth.service';
import {UserRole} from '../../../../../domain/entities/auth-user';

@Component({
    selector: 'app-task-form',
    standalone: true,
    imports: [
        CommonModule,
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        MatProgressBarModule
    ],
    templateUrl: './task-form.html',
    styleUrls: ['./task-form.scss']
})
export class TaskForm implements OnInit {
    private fb = inject(NonNullableFormBuilder);
    private titleService = inject(Title);
    private taskService = inject(TaskService);
    private topicService = inject(TopicService);
    private categoryService = inject(CategoryService);
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private notificationService = inject(NotificationService);
    private authService = inject(AuthenticationService);
    private cdr = inject(ChangeDetectorRef);

    taskForm = this.fb.group({
        description: ['', Validators.required],
    });

    isEditMode = signal(false);
    topicName = signal('');
    categoryName = signal('');
    exerciseProposal = signal<string | null>(null);
    isLoadingExercise = signal(false);

    private taskId: number | null = null;
    private topicId: number | null = null;

    ngOnInit() {
        const currentUser = this.authService.getCurrentUser();
        if (currentUser?.role !== UserRole.TEACHER) {
            this.notificationService.openSnackBar('No tienes permisos para acceder a esta página');
            this.router.navigate(['/category-list']);
            return;
        }

        const idParam = this.route.snapshot.paramMap.get('id');
        const topicIdParam = this.route.snapshot.queryParamMap.get('topicId');

        this.taskId = idParam ? Number(idParam) : null;
        this.topicId = topicIdParam ? Number(topicIdParam) : null;

        if (this.taskId) {
            this.isEditMode.set(true);
            this.titleService.setTitle('Editar Tarea');
            this.taskService.getTaskById(this.taskId).subscribe(task => {
                if (task) {
                    this.taskForm.patchValue({ description: task.description });
                    this.topicId = task.topicId;
                    this.loadTopicAndCategory(this.topicId);
                    this.cdr.detectChanges();
                }
            });
        } else {
            this.titleService.setTitle('Nueva Tarea');
            if (this.topicId) {
                this.loadTopicAndCategory(this.topicId);
            }
        }
    }

    private loadTopicAndCategory(topicId: number): void {
        this.topicService.getTopicById(topicId).pipe(
            switchMap(topic => {
                this.topicName.set(topic.name);
                return this.categoryService.getCategoryById(topic.categoryId);
            })
        ).subscribe(category => {
            this.categoryName.set(category.name);
        });
    }

    requestExercise(): void {
        const description = this.taskForm.get('description')?.value?.trim();
        if (!description || this.isLoadingExercise()) {
            return;
        }

        const prompt = `${this.categoryName()} - ${this.topicName()} - ${description}`;
        this.isLoadingExercise.set(true);
        this.exerciseProposal.set(null);

        this.taskService.generateExercise(prompt).subscribe({
            next: (res) => {
                this.exerciseProposal.set(res.taskProposal);
                this.isLoadingExercise.set(false);
            },
            error: () => {
                this.notificationService.openSnackBar('Error al generar el ejercicio');
                this.isLoadingExercise.set(false);
            }
        });
    }

    saveTask() {
        if (this.taskForm.invalid) {
            return;
        }

        const {description} = this.taskForm.getRawValue();
        const task = new Task(this.taskId, description, this.topicId!);

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

    onCancel() {
        if (this.topicId) {
            this.router.navigate(['/topic-detail', this.topicId]);
        } else {
            this.router.navigate(['/category-list']);
        }
    }
}
