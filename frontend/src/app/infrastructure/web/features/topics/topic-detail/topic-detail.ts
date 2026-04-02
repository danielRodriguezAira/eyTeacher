import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {MatListModule} from '@angular/material/list';
import {map} from 'rxjs';
import {Topic} from '../../../../../domain/entities/topic';
import {TopicService} from '../../../services/topic.service';
import {NotificationService} from '../../../services/notification.service';
import {UserRole} from "../../../../../domain/entities/auth-user";
import {AuthenticationService} from '../../../services/auth.service';
import {toSignal} from '@angular/core/rxjs-interop';
import {SafeHtmlPipe} from '../../../../../shared/pipes/safe-html.pipe';

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
    private router = inject(Router);
    private notificationService = inject(NotificationService);
    private authService = inject(AuthenticationService);

    topic = signal<Topic | undefined>(undefined);
    userRole = toSignal(this.authService.getCurrentUserObservable().pipe(
        map(user => user?.role ?? null)
    ), {initialValue: null});

    UserRole = UserRole;

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        if (id) {
            this.topicService.getTopicById(id).subscribe(t => this.topic.set(t));
        }
    }

    editTopic(topic: Topic) {
        this.router.navigate(['/topic-edit', topic.id]);
    }

    subscribeStudents(topic: Topic) {
        this.router.navigate(['/topic-subscribe', topic.id]);
    }

    deleteTopic(topic: Topic) {
        if (confirm(`¿Seguro que quieres borrar el tema "${topic.name}"?`)) {
            this.topicService.deleteTopic(topic.id!).subscribe({
                next: () => {
                    this.notificationService.openSnackBar('Tema borrado correctamente');
                    this.router.navigate(['/category-detail', topic.categoryId]);
                },
                error: (err) => {
                    this.notificationService.openSnackBar('Error al borrar el tema');
                    console.error(err);
                }
            });
        }
    }

    addTask(topicId: number | null) {
        this.router.navigate(['/task-add'], {queryParams: {topicId}});
    }

    viewTask(taskId: number) {
        this.router.navigate(['/task-detail', taskId]);
    }
}
