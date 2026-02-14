import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {ActivatedRoute, ParamMap, Router} from '@angular/router';
import {Component, inject, OnInit} from '@angular/core';
import {Title} from '@angular/platform-browser';
import {AuthenticationService} from '../../../../services/auth.service';
import {NotificationService} from '../../../../services/notification.service';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressBarModule} from '@angular/material/progress-bar';
import {MatIconModule} from '@angular/material/icon';

interface PasswordResetForm {
  newPassword: FormControl<string | null>;
  newPasswordConfirm: FormControl<string | null>;
}

@Component({
  selector: 'app-password-reset',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressBarModule,
    MatIconModule
  ],
  templateUrl: './password-reset.html',
  styleUrls: ['./password-reset.css']
})
export class PasswordReset implements OnInit {
  private token!: string;
  email!: string;
  form!: FormGroup<PasswordResetForm>;
  loading = false;
  hideNewPassword = true;
  hideNewPasswordConfirm = true;

  private activeRoute = inject(ActivatedRoute);
  private router = inject(Router);
  private authService = inject(AuthenticationService);
  private notificationService = inject(NotificationService);
  private titleService = inject(Title);

  constructor() {
    this.titleService.setTitle('angular-material-template - Password Reset');
  }

  ngOnInit() {
    this.activeRoute.queryParamMap.subscribe((params: ParamMap) => {
      this.token = (params.get('token') ?? '') + '';
      this.email = (params.get('email') ?? '') + '';

      if (!this.token || !this.email) {
        this.router.navigate(['/']);
      }
    });

    this.form = new FormGroup<PasswordResetForm>({
      newPassword: new FormControl<string | null>('', {validators: [Validators.required]}),
      newPasswordConfirm: new FormControl<string | null>('', {validators: [Validators.required]})
    });
  }

  resetPassword() {
    const password = this.form.get('newPassword')?.value ?? '';
    const passwordConfirm = this.form.get('newPasswordConfirm')?.value ?? '';

    if (password !== passwordConfirm) {
      this.notificationService.openSnackBar('Passwords do not match');
      return;
    }

    this.loading = true;

    this.authService.passwordReset(this.email, this.token, password, passwordConfirm)
      .subscribe({
        next: () => {
          this.notificationService.openSnackBar('Your password has been changed.');
          this.router.navigate(['/auth/login']);
        },
        error: (error: any) => {
          this.notificationService.openSnackBar(error.error);
          this.loading = false;
        }
      });
  }

  cancel() {
    this.router.navigate(['/']);
  }
}
