import {inject, Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, map} from 'rxjs/operators';
import moment from 'moment';
import {Observable, throwError} from 'rxjs';
import {AuthUser, UserRole} from '../../../domain/entities/auth-user';
import {AuthenticationServicePort} from '../../../application/services/auth.service.port';
import {
  AuthError
} from '../../../domain/errors/auth.errors';
import {getErrorMessage} from '../../../domain/errors/error-codes';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService implements AuthenticationServicePort {
  http = inject(HttpClient);
  localStorage = inject<Storage>('LOCALSTORAGE' as any);

  private readonly API_URL = '/api/v1/auth';

  constructor() {
  }

  login(email: string, password: string, role: UserRole): Observable<boolean> {
    const loginRole = role === UserRole.TEACHER ? 'TEACHER' : 'STUDENT';
    return this.http.post<AuthUser>(`${this.API_URL}/login`, {
      email,
      password,
      role: loginRole
    }).pipe(
      map(user => {
        if (user && user.token) {
          // Aseguramos que el rol del objeto guardado coincida con el UserRole enum
          const currentUser: AuthUser = {
            ...user,
            role: role,
            // El backend podría no devolver todos estos campos, nos aseguramos de que existan
            firstName: user.firstName || '',
            lastName: user.lastName || '',
            email: user.email || email,
            id: user.id || '',
            expiration: user.expiration || moment().add(1, 'days').toISOString()
          };
          this.localStorage.setItem('currentUser', JSON.stringify(currentUser));
          return true;
        }
        return false;
      }),
      catchError((error: HttpErrorResponse) => {
        return throwError(() => new AuthError(getErrorMessage(error.error)));
      })
    );
  }

  register(email: string, password: string, firstName: string, lastName: string): Observable<boolean> {
    return this.http.post<any>(`${this.API_URL}/register`, {
      email,
      password,
      firstName,
      lastName
    }).pipe(
      map(() => true),
      catchError((error: HttpErrorResponse) => {
        return throwError(() => new AuthError(getErrorMessage(error.error)));
      })
    );
  }

  updateProfile(email: string, firstName: string, lastName: string): Observable<boolean> {
    const id = this.getCurrentUser().id;
    return this.http.put<void>(`${this.API_URL}/profile`, {
      id,
      email,
      firstName,
      lastName
    }).pipe(
      map(() => true),
      catchError((error: HttpErrorResponse) => {
        return throwError(() => new AuthError(getErrorMessage(error.error)));
      })
    );
  }

  updatePassword(oldPassword: string, newPassword: string): Observable<boolean>  {
    const id = this.getCurrentUser().id;
    return this.http.post<void>(`${this.API_URL}/update-password`, {
      id,
      oldPassword,
      newPassword
    }).pipe(
      map(() => true),
      catchError((error: HttpErrorResponse) => {
        return throwError(() => new AuthError(getErrorMessage(error.error)));
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

}
