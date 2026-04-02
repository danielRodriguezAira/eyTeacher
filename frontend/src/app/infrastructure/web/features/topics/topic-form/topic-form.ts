import {ChangeDetectorRef, Component, inject, OnInit, signal} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {RichTextEditor} from '../../../../../shared/components/rich-text-editor/rich-text-editor';
import {TopicService} from '../../../services/topic.service';
import {Topic} from '../../../../../domain/entities/topic';
import {NotificationService} from '../../../services/notification.service';
import {AuthenticationService} from '../../../services/auth.service';
import {UserRole} from '../../../../../domain/entities/auth-user';

@Component({
    selector: 'app-topic-form',
    standalone: true,
    imports: [
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        RichTextEditor,
    ],
    templateUrl: './topic-form.html',
    styleUrls: ['./topic-form.scss']
})
export class TopicForm implements OnInit {
    private fb = inject(NonNullableFormBuilder);
    private titleService = inject(Title);
    private topicService = inject(TopicService);
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private notificationService = inject(NotificationService);
    private authService = inject(AuthenticationService);
    private cdr = inject(ChangeDetectorRef);

    topicForm = this.fb.group({
        name: ['', Validators.required],
        description: ['', Validators.required],
    });

    isEditMode = signal(false);
    private topicId: number | null = null;
    private categoryId: number | null = null;

    ngOnInit() {
        const currentUser = this.authService.getCurrentUser();
        if (currentUser?.role !== UserRole.TEACHER) {
            this.notificationService.openSnackBar('No tienes permisos para acceder a esta página');
            this.router.navigate(['/category-list']);
            return;
        }

        const idParam = this.route.snapshot.paramMap.get('id');
        const categoryIdParam = this.route.snapshot.queryParamMap.get('categoryId');
        
        this.topicId = idParam ? Number(idParam) : null;
        this.categoryId = categoryIdParam ? Number(categoryIdParam) : null;

        if (this.topicId) {
            this.isEditMode.set(true);
            this.titleService.setTitle('Editar Tema');
            this.topicService.getTopicById(this.topicId).subscribe(topic => {
                if (topic) {
                    this.topicForm.patchValue({
                        name: topic.name,
                        description: topic.description
                    });
                    this.categoryId = topic.categoryId;
                    this.cdr.detectChanges();
                }
            });
        } else {
            this.titleService.setTitle('Nuevo Tema');
        }
    }

    saveTopic() {
        if (this.topicForm.invalid) {
            return;
        }

        const {name, description} = this.topicForm.getRawValue();
        const topic = new Topic(
            this.topicId,
            name,
            description,
            this.categoryId!
        );

        this.topicService.saveTopic(topic).subscribe({
            next: () => {
                this.notificationService.openSnackBar('Tema guardado correctamente');
                this.router.navigate(['/category-detail', this.categoryId]);
            },
            error: (error) => {
                this.notificationService.openSnackBar(error.error || 'Error al guardar el tema');
            }
        });
    }

    onCancel() {
        if (this.categoryId) {
            this.router.navigate(['/category-detail', this.categoryId]);
        } else {
            this.router.navigate(['/category-list']);
        }
    }
}
