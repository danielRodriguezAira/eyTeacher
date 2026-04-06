import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, Router, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {FormsModule} from '@angular/forms';
import {map} from 'rxjs';
import {SafeHtmlPipe} from '../../../../../shared/pipes/safe-html.pipe';
import {RichTextEditor} from '../../../../../shared/components/rich-text-editor/rich-text-editor';
import {Solution} from '../../../../../domain/entities/solution';
import {Correction} from '../../../../../domain/entities/correction';
import {TaskService} from '../../../services/task.service';
import {SolutionService} from '../../../services/solution.service';
import {CorrectionService} from '../../../services/correction.service';
import {NotificationService} from '../../../services/notification.service';
import {UserRole} from "../../../../../domain/entities/auth-user";
import {AuthenticationService} from '../../../services/auth.service';
import {toSignal} from '@angular/core/rxjs-interop';

@Component({
    selector: 'app-solution-detail',
    standalone: true,
    imports: [
        CommonModule,
        MatCardModule,
        MatButtonModule,
        MatIconModule,
        RouterLink,
        MatFormFieldModule,
        MatInputModule,
        FormsModule,
        SafeHtmlPipe,
        RichTextEditor,
    ],
    templateUrl: './solution-detail.html',
    styleUrl: './solution-detail.scss'
})
export class SolutionDetail implements OnInit {
    private route = inject(ActivatedRoute);
    private router = inject(Router);
    private taskService = inject(TaskService);
    private solutionService = inject(SolutionService);
    private authService = inject(AuthenticationService);
    private correctionService = inject(CorrectionService);
    private notificationService = inject(NotificationService);

    solution = signal<Solution | undefined>(undefined);
    taskDescription = signal('');
    userRole = toSignal(this.authService.getCurrentUserObservable().pipe(
        map(user => user?.role ?? null)
    ), {initialValue: null});

    showCorrectionForm = signal(false);
    correctionText = signal('');

    UserRole = UserRole;

    ngOnInit(): void {
        const solutionId = Number(this.route.snapshot.paramMap.get('id'));

        if (solutionId) {
            this.solutionService.getSolutionById(solutionId).subscribe(solution => {
                this.taskDescription.set(solution.task.description);
                this.solution.set(solution);
            });
        }
    }

    startCorrection() {
        this.showCorrectionForm.set(true);
    }

    sendCorrection() {
        const sol = this.solution();
        if (sol && sol.id) {
            const correction: Correction = {
                description: this.correctionText(),
                solutionId: sol.id
            };
            this.correctionService.addCorrection(correction).subscribe({
                next: (savedCorrection) => {
                    this.solution.update(s => s ? {...s, correction: savedCorrection} : undefined);
                    this.router.navigate(['/task-detail', sol.task.id]);
                    this.notificationService.openSnackBar('Corrección enviada correctamente');
                },
                error: () => {
                    this.notificationService.openSnackBar('Error al enviar la corrección');
                }
            });
        }
    }
}
