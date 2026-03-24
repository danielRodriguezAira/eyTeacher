import {HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {Router} from '@angular/router';
import {catchError, throwError} from 'rxjs';
import {AuthenticationService} from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const localStorage = inject<Storage>('LOCALSTORAGE' as any);
    const router = inject(Router);
    const authService = inject(AuthenticationService);
    const currentUserRaw = localStorage?.getItem('currentUser');

    const isAuthUrl = req.url.includes('/api/v1/auth/login') || req.url.includes('/api/v1/auth/register');
    const isAiUrl = req.url.includes('/ai-api/');

    if (!currentUserRaw && !isAuthUrl && !isAiUrl) {
        router.navigate(['/login']);
        return next(req);
    }

    let requestToProcess = req;

    if (currentUserRaw) {
        try {
            const currentUser = JSON.parse(currentUserRaw);
            const token = currentUser?.token;

            if (token && !isAuthUrl) {
                requestToProcess = req.clone({
                    headers: req.headers.set('Authorization', `Bearer ${token}`)
                });
            }
        } catch (e) {
            console.error('Error parsing user from localStorage', e);
        }
    }

    return next(requestToProcess).pipe(
        catchError((error) => {
            const isAuthUrl = req.url.includes('/api/v1/auth/login') || req.url.includes('/api/v1/auth/register');
            const isAiUrl = req.url.includes('/ai-api/');
            if (error.status === 401 && !isAuthUrl && !isAiUrl) {
                console.error('Session expired or unauthorized for URL:', req.url);
                authService.logout();
                router.navigate(['/login']);
            }
            return throwError(() => error);
        })
    );
};
