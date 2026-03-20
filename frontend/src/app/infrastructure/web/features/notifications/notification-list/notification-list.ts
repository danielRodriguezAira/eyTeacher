import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {NotificationService} from '../../../services/notification.service';
import {AuthenticationService} from '../../../services/auth.service';
import {Notification} from '../../../../../domain/entities/notification';
import {Router} from '@angular/router';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [CommonModule, MatListModule, MatIconModule, MatCardModule],
  templateUrl: './notification-list.html',
  styleUrl: './notification-list.scss'
})
export class NotificationList implements OnInit {
  private notificationService = inject(NotificationService);
  private authService = inject(AuthenticationService);
  private router = inject(Router);

  notifications = signal<Notification[]>([]);

  ngOnInit() {
    this.loadNotifications();
  }

  loadNotifications() {
    const user = this.authService.getCurrentUser();
    if (user && user.id) {
      this.notificationService.getNotificationsByOwner(user.id).subscribe(
        notifications => this.notifications.set(notifications)
      );
    }
  }

  onNotificationClick(notification: Notification) {
    if (!notification.read) {
      this.notificationService.markAsRead(notification.id).subscribe(() => {
        this.updateNotificationInList(notification.id, true);
      });
    }
  }

  private updateNotificationInList(id: number, isRead: boolean) {
    this.notifications.update(list =>
      list.map(n => n.id === id ? {...n, read: isRead} : n)
    );
  }
}
