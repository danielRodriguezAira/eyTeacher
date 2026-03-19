import {inject, Injectable} from '@angular/core';
import {MatSnackBar} from '@angular/material/snack-bar';
import {HttpClient} from '@angular/common/http';
import {Notification} from '../../../domain/entities/notification';
import {BehaviorSubject, Observable, tap} from 'rxjs';

@Injectable({
    providedIn: 'root'
})
export class NotificationService {

    private snackBar = inject(MatSnackBar);
    private http = inject(HttpClient);
    private readonly API_URL = '/api/v1/notifications';

    private refreshNotificationsSubject = new BehaviorSubject<void>(undefined);

    constructor() {
    }

    public getNotificationsByOwner(ownerId: string): Observable<Notification[]> {
        return this.http.get<Notification[]>(`${this.API_URL}/owner/${ownerId}`);
    }

    public getRefreshObservable(): Observable<void> {
        return this.refreshNotificationsSubject.asObservable();
    }

    public notifyChange(): void {
        this.refreshNotificationsSubject.next();
    }

    public markAsRead(id: number): Observable<void> {
        return this.http.get<void>(`${this.API_URL}/mark-as-read/${id}`).pipe(
            tap(() => this.notifyChange())
        );
    }

    public openSnackBar(message: string) {
        this.snackBar.open(message, '', {
            duration: 10000
        });
    }
}
