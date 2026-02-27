import {Component, inject, OnInit, signal} from '@angular/core';
import {Router} from '@angular/router';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Title} from '@angular/platform-browser';
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


interface LoginForm {
    email: FormControl<string | null>;
    password: FormControl<string | null>;
    role: FormControl<UserRole | null>;
    rememberMe: FormControl<boolean | null>;
}

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
    styleUrls: ['./login.css']
})
export class Login implements OnInit {
    loginForm!: FormGroup<LoginForm>;
    loading = signal(false);

    roles = Object.values(UserRole);

    private router = inject(Router);
    private titleService = inject(Title);
    private notificationService = inject(NotificationService);
    private authenticationService = inject(AuthenticationService);

    ngOnInit() {
        this.titleService.setTitle('angular-material-template - Login');
        this.authenticationService.logout();
        this.createForm();
    }

    private createForm() {
        const savedUserEmail = localStorage.getItem('savedUserEmail');

        this.loginForm = new FormGroup<LoginForm>({
            email: new FormControl<string | null>(savedUserEmail, {
                nonNullable: false,
                validators: [Validators.required, Validators.email]
            }),
            password: new FormControl<string | null>('', {nonNullable: false, validators: [Validators.required]}),
            role: new FormControl<UserRole | null>(UserRole.STUDENT, {
                nonNullable: false,
                validators: [Validators.required]
            }),
            rememberMe: new FormControl<boolean | null>(savedUserEmail !== null)
        });
    }

    login() {
        const email = this.loginForm.get('email')?.value ?? '';
        const password = this.loginForm.get('password')?.value ?? '';
        const role = this.loginForm.get('role')?.value ?? UserRole.STUDENT;
        const rememberMe = this.loginForm.get('rememberMe')?.value ?? false;

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
                    this.router.navigate(['/']);
                },
                error: (error) => {
                    if (error instanceof DomainError) {
                        this.notificationService.openSnackBar(error.message);
                    } else {
                        this.notificationService.openSnackBar(error.message || 'Ocurrió un error inesperado durante la autenticación');
                    }
                    this.loading.set(false);
                }
            });
    }

    createAccount() {
        this.router.navigate(['/register']);
    }
}
