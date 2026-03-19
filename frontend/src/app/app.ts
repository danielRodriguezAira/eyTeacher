import {Component, computed, inject, signal} from '@angular/core';
import {Router, RouterLink, RouterLinkActive, RouterOutlet} from '@angular/router';
import {CommonModule} from '@angular/common';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatMenuModule} from '@angular/material/menu';
import {MatDividerModule} from '@angular/material/divider';
import {Observable, of, switchMap} from 'rxjs';
import {AuthenticationService} from './infrastructure/web/services/auth.service';
import {CategoryService} from './infrastructure/web/services/category.service';
import {Category} from './domain/entities/category';
import {NotificationService} from './infrastructure/web/services/notification.service';
import {Notification} from './domain/entities/notification';
import {toSignal} from '@angular/core/rxjs-interop';

@Component({
    selector: 'app-root',
    standalone: true,
    imports: [
        CommonModule,
        RouterOutlet,
        RouterLink,
        RouterLinkActive,
        MatToolbarModule,
        MatSidenavModule,
        MatListModule,
        MatIconModule,
        MatButtonModule,
        MatMenuModule,
        MatDividerModule
    ],
    templateUrl: './app.html',
    styleUrl: './app.css',
    animations: [
        trigger('expandCollapse', [
            state('collapsed', style({
                height: '0px',
                minHeight: '0',
                display: 'none'
            })),
            state('expanded', style({
                height: '*'
            })),
            transition('expanded <=> collapsed', animate('300ms cubic-bezier(0.4, 0, 0.2, 1)'))
        ])
    ]
})
export class App {
    protected readonly title = signal('EyTeacher');
    protected categories$: Observable<Category[]>;

    private readonly router = inject(Router);
    private readonly auth = inject(AuthenticationService);
    private readonly categoryService = inject(CategoryService);
    private readonly notificationService = inject(NotificationService);

    notifications = toSignal(this.auth.getCurrentUserObservable().pipe(
        switchMap(user => {
            if (user && user.id) {
                return this.notificationService.getRefreshObservable().pipe(
                    switchMap(() => this.notificationService.getNotificationsByOwner(user.id))
                );
            }
            return of([] as Notification[]);
        })
    ), {initialValue: [] as Notification[]});

    hasUnreadNotifications = computed(() => {
        return this.notifications()?.some(n => !n.read) ?? false;
    });

    constructor() {
        this.categories$ = this.categoryService.getCategories();
    }

    isPublicRoute(): boolean {
        const url = this.router.url;
        if (url.startsWith('/login')) {
            return true;
        }
        // Para '/register', es pública (sin layout) siempre
        if (url.startsWith('/register')) {
            return true;
        }
        return false;
    }

    onLogout() {
        this.auth.logout();
        this.router.navigate(['/login']);
    }
}
