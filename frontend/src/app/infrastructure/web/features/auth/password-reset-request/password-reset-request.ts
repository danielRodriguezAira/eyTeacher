import {Router} from '@angular/router';
import {Component, inject, OnInit} from '@angular/core';
import {FormControl, FormGroup, ReactiveFormsModule, Validators} from '@angular/forms';
import {Title} from '@angular/platform-browser';

import {NotificationService} from '../../../../services/notification.service';
import {AuthenticationService} from '../../../../services/auth.service';
import {MatCardModule} from '@angular/material/card';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import {MatProgressBarModule} from '@angular/material/progress-bar';

interface PasswordResetRequestForm {
  email: FormControl<string | null>;
}

@Component({
  selector: 'app-password-reset-request',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatProgressBarModule
  ],
  templateUrl: './password-reset-request.html',
  styleUrls: ['./password-reset-request.css']
})
export class PasswordResetRequest implements OnInit {
  private email!: string;
  form!: FormGroup<PasswordResetRequestForm>;
  loading = false;

  private authService = inject(AuthenticationService);
  private notificationService = inject(NotificationService);
  private titleService = inject(Title);
  private router = inject(Router);

  ngOnInit() {
    this.titleService.setTitle('angular-material-template - Password Reset Request');

    this.form = new FormGroup<PasswordResetRequestForm>({
      email: new FormControl<string | null>('', {validators: [Validators.required, Validators.email]})
    });

    this.form.get('email')?.valueChanges
      .subscribe((val: string | null) => {
        this.email = (val ?? '').toLowerCase();
      });
  }

  resetPassword() {
    this.loading = true;
    this.authService.passwordResetRequest(this.email)
      .subscribe({
        next: () => {
          this.router.navigate(['/auth/login']);
          this.notificationService.openSnackBar('Password verification mail has been sent to your email address.');
        },
        error: (error) => {
          this.loading = false;
          this.notificationService.openSnackBar(error.error);
        }
      });
  }

  cancel() {
    this.router.navigate(['/']);
  }
}
