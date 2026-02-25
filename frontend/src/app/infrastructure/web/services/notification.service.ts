import {inject, Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private snackBar = inject(MatSnackBar);

  constructor() {
  }

  public openSnackBar(message: string) {
    this.snackBar.open(message, '', {
      duration: 5000
    });
  }
}
