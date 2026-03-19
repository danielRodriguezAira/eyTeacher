import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {FormsModule} from '@angular/forms';
import {SolutionService} from '../../../services/solution.service';
import {NotificationService} from '../../../services/notification.service';

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
        FormsModule
    ],
    templateUrl: './solution-form.html',
    styleUrl: './solution-form.scss'
})
export class SolutionForm implements OnInit {
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private solutionService = inject(SolutionService);
    private notificationService = inject(NotificationService);

    taskId = signal<number | null>(null);
    description = signal('');

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('taskId'));
        if (id) {
            this.taskId.set(id);
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

    cancel() {
        const id = this.taskId();
        if (id) {
            this.router.navigate(['/task-detail', id]);
        } else {
            this.router.navigate(['/']);
        }
    }
}
