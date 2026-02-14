import {Component, inject, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatDividerModule} from '@angular/material/divider';
import {AuthenticationService} from '../../../services/auth.service';

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
    MatDividerModule
  ],
  templateUrl: './account-page.html',
  styleUrls: ['./account-page.css']
})
export class AccountPage implements OnInit {

  profileForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
  });
  passwordForm = new FormGroup({
    currentPassword: new FormControl('', Validators.required),
    newPassword: new FormControl('', [Validators.required, Validators.minLength(6)]),
    newPasswordConfirm: new FormControl('', Validators.required),
  });

  hideCurrentPassword = true;
  hideNewPassword = true;
  hideConfirmPassword = true;

  private titleService = inject(Title);
  private authService = inject(AuthenticationService);

  ngOnInit() {
    this.titleService.setTitle('Account');
    const currentUser = this.authService.getCurrentUser();
    if (currentUser) {
      this.profileForm.patchValue({
        email: currentUser.email,
        firstName: currentUser.firstName,
        lastName: currentUser.lastName
      });
    }
  }

  saveProfile() {
    if (this.profileForm.valid) {
      console.log('Saving profile:', this.profileForm.value);
      alert('Perfil actualizado (simulado)');
    }
  }

  changePassword() {
    if (this.passwordForm.valid) {
      const {currentPassword, newPassword, newPasswordConfirm} = this.passwordForm.value;

      if (newPassword !== newPasswordConfirm) {
        alert('Las nuevas contraseñas no coinciden.');
        return;
      }

      const email = this.authService.getCurrentUser().email;
      this.authService.changePassword(email, currentPassword, newPassword)
        .subscribe({
          next: () => {
            this.passwordForm.reset();
            alert('Contraseña cambiada con éxito.');
          },
          error: (err) => {
            alert(err.error || 'Error al cambiar la contraseña');
          }
        });
    }
  }
}
