import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatCardModule} from '@angular/material/card';
import {MatButtonModule} from '@angular/material/button';
import {NotificationService} from '../../../services/notification.service';
import {AuthenticationService} from '../../../services/auth.service';
import {Notification} from '../../../../../domain/entities/notification';
import {Router} from '@angular/router';

const PAGE_SIZE = 10;

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [CommonModule, MatListModule, MatIconModule, MatCardModule, MatButtonModule],
  templateUrl: './notification-list.html',
  styleUrl: './notification-list.scss'
})
export class NotificationList implements OnInit {
  private notificationService = inject(NotificationService);
  private authService = inject(AuthenticationService);
  private router = inject(Router);

  notifications = signal<Notification[]>([]);
  hasMore = signal(false);
  private currentPage = 0;
  private ownerId = '';

  ngOnInit() {
    const user = this.authService.getCurrentUser();
    if (user?.id) {
      this.ownerId = user.id;
      this.loadNotifications();
    }
  }

  loadNotifications() {
    this.notificationService.getNotificationsByOwner(this.ownerId, this.currentPage, PAGE_SIZE).subscribe(page => {
      this.notifications.update(existing => [...existing, ...page.content]);
      this.hasMore.set(page.hasNext);
      this.currentPage++;
    });
  }

  onNotificationClick(notification: Notification) {
    if (!notification.read) {
      this.notificationService.markAsRead(notification.id).subscribe(() => {
        this.updateNotificationInList(notification.id, true);
      });
    }
    this.navigateTo(notification);
  }

  private navigateTo(notification: Notification): void {
    const routes: Record<string, string[]> = {
      TASK:       ['/task-detail', String(notification.entityId)],
      SOLUTION:   ['/solution', String(notification.entityId)],
      CORRECTION: ['/solution', String(notification.entityId)],
      TOPIC:      ['/topic-detail', String(notification.entityId)],
    };
    const route = routes[notification.entityType];
    if (route) {
      this.router.navigate(route);
    }
  }

  private updateNotificationInList(id: number, isRead: boolean) {
    this.notifications.update(list =>
      list.map(n => n.id === id ? {...n, read: isRead} : n)
    );
  }
}
