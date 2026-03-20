import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatIconModule} from '@angular/material/icon';
import {ReactiveFormsModule, FormBuilder, Validators, FormGroup, AbstractControl, ValidationErrors} from '@angular/forms';
import {TopicService} from '../../../services/topic.service';
import {AuthenticationService} from '../../../services/auth.service';
import {NotificationService} from '../../../services/notification.service';
import {Topic} from '../../../../../domain/entities/topic';

@Component({
    selector: 'app-subscribe-students',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatButtonModule,
        MatFormFieldModule,
        MatInputModule,
        MatIconModule,
        ReactiveFormsModule,
        RouterLink
    ],
    templateUrl: './subscribe-students.html',
    styleUrl: './subscribe-students.scss'
})
export class SubscribeStudents implements OnInit {
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private fb = inject(FormBuilder);
    private topicService = inject(TopicService);
    private authService = inject(AuthenticationService);
    private notificationService = inject(NotificationService);

    topic = signal<Topic | undefined>(undefined);
    subscribeForm: FormGroup;

    constructor() {
        this.subscribeForm = this.fb.group({
            emails: ['', [Validators.required, this.emailsValidator]]
        });
    }

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        if (id) {
            this.topicService.getTopicById(id).subscribe({
                next: (t) => this.topic.set(t),
                error: (err) => {
                    this.notificationService.openSnackBar('Error al cargar el tema');
                    console.error(err);
                }
            });
        }
    }

    private emailsValidator(control: AbstractControl): ValidationErrors | null {
        if (!control.value) return null;
        const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        const emails = control.value.split(',').map((e: string) => e.trim());
        const allValid = emails.every((e: string) => emailPattern.test(e));
        return allValid ? null : {invalidEmails: true};
    }

    onSubmit(): void {
        if (this.subscribeForm.valid && this.topic()) {
            const topicId = this.topic()!.id!;
            const ownerId = this.authService.getCurrentUser().id;
            const studentEmailList = this.subscribeForm.value.emails
                .split(',')
                .map((e: string) => e.trim())
                .filter((e: string) => e.length > 0);

            this.topicService.subscribeStudents(topicId, ownerId, studentEmailList).subscribe({
                next: () => {
                    this.notificationService.openSnackBar('Subscripciones enviadas correctamente');
                    this.router.navigate(['/topic-detail', topicId]);
                },
                error: (err) => {
                    this.notificationService.openSnackBar('Error al enviar las subscripciones');
                    console.error(err);
                }
            });
        }
    }
}
