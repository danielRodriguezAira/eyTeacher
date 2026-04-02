import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {FormsModule} from '@angular/forms';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {RichTextEditor} from '../../../../../shared/components/rich-text-editor/rich-text-editor';
import {SafeHtmlPipe} from '../../../../../shared/pipes/safe-html.pipe';
import {SolutionService} from '../../../services/solution.service';
import {NotificationService} from '../../../services/notification.service';
import {TaskService} from '../../../services/task.service';

@Component({
    selector: 'app-solution-form',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        MatFormFieldModule,
        MatInputModule,
        MatProgressBarModule,
        FormsModule,
        RichTextEditor,
        SafeHtmlPipe,
    ],
    templateUrl: './solution-form.html',
    styleUrl: './solution-form.scss'
})
export class SolutionForm implements OnInit {
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private solutionService = inject(SolutionService);
    private notificationService = inject(NotificationService);
    private taskService = inject(TaskService);

    taskId = signal<number | null>(null);
    taskDescription = signal('');
    description = signal('');
    hint = signal<string | null>(null);
    hintRequested = signal(false);

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('taskId'));
        if (id) {
            this.taskId.set(id);
            this.taskService.getTaskById(id).subscribe({
                next: (task) => {
                    this.taskDescription.set(task.description);
                },
                error: (err) => {
                    console.error('Error fetching task description:', err);
                }
            });
        } else {
            this.router.navigate(['/']);
        }
    }

    sendSolution() {
        const id = this.taskId();
        if (id && this.description()) {
            this.solutionService.addSolution({
                description: this.description(),
                taskId: id
            }).subscribe({
                next: () => {
                    this.notificationService.openSnackBar('Solución enviada correctamente');
                    this.router.navigate(['/task-detail', id]);
                },
                error: (err) => {
                    this.notificationService.openSnackBar('Error al enviar la solución');
                    console.error(err);
                }
            });
        }
    }

    requestHint() {
        if (this.taskDescription() && !this.hintRequested()) {
            this.hintRequested.set(true);
            this.solutionService.getHint(this.taskDescription()).subscribe({
                next: (res) => {
                    this.hint.set(res.hint);
                },
                error: (err) => {
                    this.notificationService.openSnackBar('Error al obtener la pista');
                    console.error(err);
                    this.hintRequested.set(false);
                }
            });
        }
    }

    cancel() {
        const id = this.taskId();
        if (id) {
            this.router.navigate(['/task-detail', id]);
        } else {
            this.router.navigate(['/']);
        }
    }
}
