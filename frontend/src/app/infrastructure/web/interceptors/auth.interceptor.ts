import {HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const localStorage = inject<Storage>('LOCALSTORAGE' as any);
    const currentUserRaw = localStorage?.getItem('currentUser');

    if (currentUserRaw) {
        try {
            const currentUser = JSON.parse(currentUserRaw);
            const token = currentUser?.token;

            if (token && !req.url.includes('/api/v1/auth/login') && !req.url.includes('/api/v1/auth/register')) {
                const clonedReq = req.clone({
                    headers: req.headers.set('Authorization', `Bearer ${token}`)
                });
                return next(clonedReq);
            }
        } catch (e) {
            console.error('Error parsing user from localStorage', e);
        }
    }

    return next(req);
};
