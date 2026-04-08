import {Component, inject, OnInit, signal} from '@angular/core';
import {NonNullableFormBuilder, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {Router, RouterLink} from '@angular/router';
import {AuthenticationService} from '../../services/auth.service';
import {NotificationService} from '../../services/notification.service';
import {DomainError} from '../../../../domain/errors/auth.errors';

@Component({
    selector: 'app-account-page',
    standalone: true,
    imports: [
        ReactiveFormsModule,
        MatCardModule,
        MatFormFieldModule,
        MatInputModule,
        MatButtonModule,
        MatIconModule,
        MatDividerModule,
        RouterLink
    ],
    templateUrl: './account-page.html',
    styleUrls: ['./account-page.scss']
})
export class AccountPage implements OnInit {
    isLoggedIn = signal(false);
    hidePassword = signal(true);
    hideCurrentPassword = signal(true);
    hideNewPassword = signal(true);
    hideConfirmPassword = signal(true);

    private fb = inject(NonNullableFormBuilder);
    private authService = inject(AuthenticationService);
    private router = inject(Router);
    private notificationService = inject(NotificationService);

    profileForm = this.fb.group({
        email: ['', [Validators.required, Validators.email]],
        firstName: ['', Validators.required],
        lastName: ['', Validators.required],
        password: ['', [Validators.required, Validators.minLength(8)]],
    });

    passwordForm = this.fb.group({
        oldPassword: ['', Validators.required],
        newPassword: ['', [Validators.required, Validators.minLength(8)]],
        newPasswordConfirm: ['', Validators.required],
    });

    ngOnInit() {
        const currentUser = this.authService.getCurrentUser();
        this.isLoggedIn.set(!!currentUser);

        if (currentUser) {
            this.profileForm.patchValue({
                email: currentUser.email,
                firstName: currentUser.firstName,
                lastName: currentUser.lastName
            });
            this.profileForm.controls.password.clearValidators();
            this.profileForm.controls.password.updateValueAndValidity();
        }
    }

    saveProfile() {
        if (this.profileForm.invalid) {
            return;
        }

        const {email, firstName, lastName} = this.profileForm.getRawValue();

        if (this.isLoggedIn()) {
            this.updateProfile(email, firstName, lastName);
        } else {
            this.register(email, firstName, lastName);
        }
    }

    private updateProfile(email: string, firstName: string, lastName: string) {
        this.authService.updateProfile(email, firstName, lastName)
            .subscribe({
                next: () => {
                    this.notificationService.openSnackBar('Perfil actualizado. Inicie sesión de nuevo');
                    this.authService.logout();
                    this.router.navigate(['/login']);
                },
                error: (error) => this.handleError(error, 'Ocurrió un error inesperado durante la actualización de los datos del usuario')
            });
    }

    private register(email: string, firstName: string, lastName: string) {
        const {password} = this.profileForm.getRawValue();
        this.authService.register(email, password, firstName, lastName)
            .subscribe({
                next: () => {
                    this.notificationService.openSnackBar('Usuario registrado con éxito');
                    this.router.navigate(['/login']);
                },
                error: (error) => this.handleError(error, 'Ocurrió un error inesperado durante el registro de usuario')
            });
    }

    updatePassword() {
        if (this.passwordForm.invalid) {
            return;
        }

        const {oldPassword, newPassword, newPasswordConfirm} = this.passwordForm.getRawValue();

        if (newPassword !== newPasswordConfirm) {
            this.notificationService.openSnackBar('Las nuevas contraseñas no coinciden.');
            return;
        }

        this.authService.updatePassword(oldPassword, newPassword)
            .subscribe({
                next: () => {
                    this.passwordForm.reset();
                    this.notificationService.openSnackBar('Contraseña cambiada con éxito.');
                    this.authService.logout();
                    this.router.navigate(['/login']);
                },
                error: (error) => this.handleError(error, 'Ocurrió un error inesperado durante la actualización de la contraseña')
            });
    }

    private handleError(error: any, defaultMessage: string) {
        const message = error instanceof DomainError ? error.message : (error.message || defaultMessage);
        this.notificationService.openSnackBar(message);
    }
}
