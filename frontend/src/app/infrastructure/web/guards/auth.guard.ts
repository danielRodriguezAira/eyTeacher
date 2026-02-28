import {CanActivateFn, Router} from '@angular/router';
import {inject} from '@angular/core';
import {AuthenticationService} from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
    const auth = inject(AuthenticationService);
    const router = inject(Router);
    const currentUser = auth.getCurrentUser();

    if (currentUser) {
        return true;
    }

    router.navigate(['/login']);
    return false;
};
