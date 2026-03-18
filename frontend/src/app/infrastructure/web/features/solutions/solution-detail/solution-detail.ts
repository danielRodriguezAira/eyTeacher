import {Component, inject, OnInit, signal} from '@angular/core';
import {ActivatedRoute, RouterLink} from '@angular/router';
import {CommonModule} from '@angular/common';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {FormsModule} from '@angular/forms';
import {map} from 'rxjs';
import {Solution} from '../../../../../domain/entities/solution';
import {TaskService} from '../../../services/task.service';
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
        FormsModule
    ],
    templateUrl: './solution-detail.html',
    styleUrl: './solution-detail.scss'
})
export class SolutionDetail implements OnInit {
    private route = inject(ActivatedRoute);
    private taskService = inject(TaskService);
    private authService = inject(AuthenticationService);

    solution = signal<Solution | undefined>(undefined);
    userRole = toSignal(this.authService.getCurrentUserObservable().pipe(
        map(user => user?.role ?? null)
    ), {initialValue: null});

    showCorrectionForm = signal(false);
    correctionText = signal('');

    UserRole = UserRole;

    ngOnInit(): void {
        const taskId = Number(this.route.snapshot.paramMap.get('taskId'));
        const solutionId = Number(this.route.snapshot.paramMap.get('id'));
        
        if (taskId && solutionId) {
            this.taskService.getTaskById(taskId).pipe(
                map(task => task.solutionList.find(s => s.id === solutionId))
            ).subscribe(s => this.solution.set(s));
        }
    }

    startCorrection() {
        this.showCorrectionForm.set(true);
    }

    sendCorrection() {
        // Lógica de envío de corrección no implementada según requerimiento
        console.log('Enviando corrección:', this.correctionText());
    }
}
