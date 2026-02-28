import {Component, inject, OnInit} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatMenu, MatMenuItem, MatMenuTrigger} from '@angular/material/menu';
import {Observable} from 'rxjs';
import {Topic} from '../../../../../domain/entities/topic';
import {TopicService} from '../../../services/topic.service';
import {NotificationService} from '../../../services/notification.service';

@Component({
    selector: 'app-topic-detail',
    standalone: true,
    imports: [CommonModule, MatCardModule, MatButtonModule, MatIconModule, RouterLink, MatMenu, MatMenuItem, MatMenuTrigger],
    templateUrl: './topic-detail.html',
    styleUrl: './topic-detail.css'
})
export class TopicDetail implements OnInit {
    topic$?: Observable<Topic>;
    private route = inject(ActivatedRoute);
    private topicService = inject(TopicService);
    private router = inject(Router);
    private notificationService = inject(NotificationService);

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        if (id) {
            this.topic$ = this.topicService.getTopicById(id);
        }
    }

    editTopic(topic: Topic) {
        this.router.navigate(['/topic-edit', topic.id]);
    }

    deleteTopic(topic: Topic) {
        const confirmed = confirm(`¿Seguro que quieres borrar el tema "${topic.name}"?`);
        if (confirmed) {
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
}
