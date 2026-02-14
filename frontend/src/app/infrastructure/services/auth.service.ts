import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {delay, mergeMap} from 'rxjs/operators';
import moment from 'moment';
import {of, throwError} from 'rxjs';
import {AuthUser, UserRole} from '../../domain/entities/auth-user';
import {AuthenticationServicePort} from '../../application/services/auth.service.port';

const MOCK_USERS: Array<{
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  isAdmin?: boolean;
  id: string;
}> = [
  {
    email: 'john.doe@gmail.com',
    password: 'password123',
    firstName: 'John',
    lastName: 'Doe',
    isAdmin: true,
    id: '12312323232'
  },
  {
    email: 'jane.smith@gmail.com',
    password: 'secret456',
    firstName: 'Jane',
    lastName: 'Smith',
    isAdmin: false,
    id: '987654321'
  }
];

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService implements AuthenticationServicePort {
  http = inject(HttpClient);
  localStorage = inject<Storage>('LOCALSTORAGE' as any);

  constructor() {
  }

  login(email: string, password: string, role: UserRole) {
    const normalizedEmail = (email || '').toLowerCase();
    return of({email: normalizedEmail, password, role})
      .pipe(
        delay(600),
        mergeMap(({email, password, role}) => {
          const matched = MOCK_USERS.find(u => u.email === email && u.password === password);
          if (!matched) {
            return throwError(() => ({error: 'Invalid email or password'}));
          }

          const currentUser: AuthUser = {
            token: 'mock-token-' + Math.random().toString(36).slice(2),
            isAdmin: !!matched.isAdmin,
            email: matched.email,
            id: matched.id,
            alias: matched.email.split('@')[0],
            expiration: moment().add(1, 'days').toISOString(),
            firstName: matched.firstName,
            lastName: matched.lastName,
            role: role
          };

          this.localStorage.setItem('currentUser', JSON.stringify(currentUser));
          return of(true);
        })
      );
  }

  logout(): void {
    // clear token remove user from local storage to log user out
    this.localStorage.removeItem('currentUser');
  }

  getCurrentUser(): any {
    const raw = this.localStorage.getItem('currentUser');
    if (!raw) {
      return null;
    }
    try {
      return JSON.parse(raw);
    } catch {
      return null;
    }
  }

  passwordResetRequest(email: string) {
    return of(true).pipe(delay(1000));
  }

  changePassword(email: string, currentPwd: string | null | undefined, newPwd: string | null | undefined) {
    return of(true).pipe(delay(1000));
  }

  passwordReset(email: string, token: string, password: string, confirmPassword: string): any {
    return of(true).pipe(delay(1000));
  }
}
