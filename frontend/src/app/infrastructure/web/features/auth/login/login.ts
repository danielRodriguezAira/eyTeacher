import {Component, inject, OnInit, signal} from '@angular/core';
import {Router} from '@angular/router';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {MatSelectModule} from '@angular/material/select';
import {AuthenticationService} from '../../../services/auth.service';
import {NotificationService} from '../../../services/notification.service';
import {UserRole} from '../../../../../domain/entities/auth-user';
import {DomainError} from '../../../../../domain/errors/auth.errors';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatProgressBarModule,
        MatSlideToggleModule,
        MatSelectModule
    ],
    templateUrl: './login.html',
    styleUrls: ['./login.scss']
})
export class Login implements OnInit {
    loading = signal(false);
    roles = Object.values(UserRole);

    private fb = inject(NonNullableFormBuilder);
    private router = inject(Router);
    private notificationService = inject(NotificationService);
    private authenticationService = inject(AuthenticationService);

    loginForm = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        password: ['', Validators.required],
        role: [UserRole.STUDENT, Validators.required],
        rememberMe: [false]
    });

    ngOnInit() {
        this.authenticationService.logout();
        this.loadSavedUser();
    }

    private loadSavedUser() {
        const savedUserEmail = localStorage.getItem('savedUserEmail');
        const savedRole = localStorage.getItem('savedRole') as UserRole | null;
        this.loginForm.patchValue({
            ...(savedUserEmail && {email: savedUserEmail, rememberMe: true}),
            ...(savedRole && {role: savedRole}),
        });
    }

    login() {
        if (this.loginForm.invalid) {
            return;
        }

        const {email, password, role, rememberMe} = this.loginForm.getRawValue();

        this.loading.set(true);
        this.authenticationService
            .login(email.toLowerCase(), password, role)
            .subscribe({
                next: () => {
                    if (rememberMe) {
                        localStorage.setItem('savedUserEmail', email);
                    } else {
                        localStorage.removeItem('savedUserEmail');
                    }
                    localStorage.setItem('savedRole', role);
                    this.router.navigate(['/']);
                },
                error: (error) => {
                    const message = error instanceof DomainError ? error.message : (error.message || 'Ocurrió un error inesperado durante la autenticación');
                    this.notificationService.openSnackBar(message);
                    this.loading.set(false);
                }
            });
    }

    createAccount() {
        this.router.navigate(['/register']);
    }
}
