import {Component, inject, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
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
    styleUrls: ['./account-page.css']
})
export class AccountPage implements OnInit {
    isLoggedIn = false;

    profileForm = new FormGroup({
        email: new FormControl('', [Validators.required, Validators.email]),
        firstName: new FormControl('', Validators.required),
        lastName: new FormControl('', Validators.required),
        password: new FormControl('', [Validators.required, Validators.minLength(8)]),
    });
    passwordForm = new FormGroup({
        oldPassword: new FormControl('', Validators.required),
        newPassword: new FormControl('', [Validators.required, Validators.minLength(8)]),
        newPasswordConfirm: new FormControl('', Validators.required),
    });

    hidePassword = true;
    hideCurrentPassword = true;
    hideNewPassword = true;
    hideConfirmPassword = true;

    private titleService = inject(Title);
    private authService = inject(AuthenticationService);
    private router = inject(Router);
    private notificationService = inject(NotificationService);

    ngOnInit() {
        this.titleService.setTitle('Account');
        const currentUser = this.authService.getCurrentUser();
        this.isLoggedIn = !!currentUser;
        if (currentUser) {
            this.profileForm.patchValue({
                email: currentUser.email,
                firstName: currentUser.firstName,
                lastName: currentUser.lastName
            });
            this.profileForm.get('password')?.clearValidators();
            this.profileForm.get('password')?.updateValueAndValidity();
        }
    }

    saveProfile() {
        if (!this.profileForm.valid) {
            return;
        }
        const {email, firstName, lastName} = this.profileForm.getRawValue();
        if (this.isLoggedIn) {
            this.updateProfile(email, firstName, lastName);
        } else {
            this.register(email, firstName, lastName);
        }
    }

    updateProfile(email: string | null, firstName: string | null, lastName: string | null) {
        this.authService.updateProfile(email!, firstName!, lastName!)
            .subscribe({
                next: () => {
                    this.notificationService.openSnackBar('Perfil actualizado. Inicie sesión de nuevo');
                    this.authService.logout();
                    this.router.navigate(['/login']);
                },
                error: (error) => {
                    if (error instanceof DomainError) {
                        this.notificationService.openSnackBar(error.message);
                    } else {
                        this.notificationService.openSnackBar(error.message || 'Ocurrió un error inesperado durante la actualización de los datos del usuario');
                    }
                }
            });
    }

    register(email: string | null, firstName: string | null, lastName: string | null) {
        const {password} = this.profileForm.getRawValue();
        this.authService.register(email!, password!, firstName!, lastName!)
            .subscribe({
                next: () => {
                    this.notificationService.openSnackBar('Usuario registrado con éxito');
                    this.router.navigate(['/login']);
                },
                error: (error) => {
                    if (error instanceof DomainError) {
                        this.notificationService.openSnackBar(error.message);
                    } else {
                        this.notificationService.openSnackBar(error.message || 'Ocurrió un error inesperado durante el registro de usuario');
                    }
                }
            });
    }

    updatePassword() {
        if (this.passwordForm.valid) {
            const {oldPassword, newPassword, newPasswordConfirm} = this.passwordForm.value;

            if (newPassword !== newPasswordConfirm) {
                this.notificationService.openSnackBar('Las nuevas contraseñas no coinciden.');
                return;
            }
            this.authService.updatePassword(oldPassword!, newPassword!)
                .subscribe({
                    next: () => {
                        this.passwordForm.reset();
                        this.notificationService.openSnackBar('Contraseña cambiada con éxito.');
                        this.authService.logout();
                        this.router.navigate(['/login']);
                    },
                    error: (error) => {
                        if (error instanceof DomainError) {
                            this.notificationService.openSnackBar(error.message);
                        } else {
                            this.notificationService.openSnackBar(error.message || 'Ocurrió un error inesperado durante la actualización de la contraseña');
                        }
                    }
                });
        }
    }
}
