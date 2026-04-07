import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatListModule} from '@angular/material/list';
import {MatDialog} from '@angular/material/dialog';
import {map} from 'rxjs';
import {Topic} from '../../../../../domain/entities/topic';
import {Task} from '../../../../../domain/entities/task';
import {TopicService} from '../../../services/topic.service';
import {TaskService} from '../../../services/task.service';
import {NotificationService} from '../../../services/notification.service';
import {UserRole} from "../../../../../domain/entities/auth-user";
import {AuthenticationService} from '../../../services/auth.service';
import {toSignal} from '@angular/core/rxjs-interop';
import {SafeHtmlPipe} from '../../../../../shared/pipes/safe-html.pipe';
import {ConfirmDialog} from '../../../../../shared/components/confirm-dialog/confirm-dialog';
import {getErrorMessage} from '../../../../../domain/errors/error-codes';

const PAGE_SIZE = 10;

@Component({
    selector: 'app-topic-detail',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterLink, MatMenu, MatMenuItem, MatMenuTrigger, MatListModule, SafeHtmlPipe],
    templateUrl: './topic-detail.html',
    styleUrl: './topic-detail.scss'
})
export class TopicDetail implements OnInit {
    private route = inject(ActivatedRoute);
    private topicService = inject(TopicService);
    private taskService = inject(TaskService);
    private router = inject(Router);
    private notificationService = inject(NotificationService);
    private authService = inject(AuthenticationService);
    private dialog = inject(MatDialog);

    topic = signal<Topic | undefined>(undefined);
    tasks = signal<Task[]>([]);
    hasMoreTasks = signal(false);
    private currentPage = 0;
    private topicId = 0;

    userRole = toSignal(this.authService.getCurrentUserObservable().pipe(
        map(user => user?.role ?? null)
    ), {initialValue: null});

    UserRole = UserRole;

    ngOnInit(): void {
        this.topicId = Number(this.route.snapshot.paramMap.get('id'));
        if (this.topicId) {
            this.topicService.getTopicById(this.topicId).subscribe({
                next: (t) => this.topic.set(t),
                error: (err) => this.notificationService.openSnackBar(getErrorMessage(err.error, 'Error al cargar el tema'))
            });
            this.loadTasks();
        }
    }

    loadTasks(): void {
        this.taskService.getTasksByTopic(this.topicId, this.currentPage, PAGE_SIZE).subscribe({
            next: (page) => {
                this.tasks.update(existing => [...existing, ...page.content]);
                this.hasMoreTasks.set(page.hasNext);
                this.currentPage++;
            },
            error: (err) => this.notificationService.openSnackBar(getErrorMessage(err.error, 'Error al cargar las tareas'))
        });
    }

    editTopic(topic: Topic) {
        this.router.navigate(['/topic-edit', topic.id]);
    }

    subscribeStudents(topic: Topic) {
        this.router.navigate(['/topic-subscribe', topic.id]);
    }

    deleteTopic(topic: Topic) {
        const ref = this.dialog.open(ConfirmDialog, {
            data: {
                title: 'Borrar tema',
                message: `¿Seguro que quieres borrar el tema "${topic.name}"?`
            }
        });
        ref.afterClosed().subscribe(confirmed => {
            if (confirmed) {
                this.topicService.deleteTopic(topic.id!).subscribe({
                    next: () => {
                        this.notificationService.openSnackBar('Tema borrado correctamente');
                        this.router.navigate(['/category-detail', topic.categoryId]);
                    },
                    error: (err) => {
                        this.notificationService.openSnackBar(getErrorMessage(err.error, 'Error al borrar el tema'));
                        console.error(err);
                    }
                });
            }
        });
    }

    addTask(topicId: number | null) {
        this.router.navigate(['/task-add'], {queryParams: {topicId}});
    }

    viewTask(taskId: number) {
        this.router.navigate(['/task-detail', taskId]);
    }
}
