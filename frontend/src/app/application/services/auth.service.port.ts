import {HttpClient} from '@angular/common/http';
import {UserRole} from '../../domain/entities/auth-user';
import {Observable} from 'rxjs';

export interface AuthenticationServicePort {
  http: HttpClient;
  localStorage: Storage;

  login(email: string, password: string, role: UserRole): Observable<boolean>;

  register(email: string, password: string, firstName: string, lastName: string): Observable<boolean>;

  updateProfile(email: string, firstName: string, lastName: string): Observable<boolean>;

  updatePassword(oldPassword: string, newPassword: string): Observable<boolean>;

  logout(): void;

  getCurrentUser(): any;
}
