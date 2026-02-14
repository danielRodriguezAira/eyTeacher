import {HttpClient} from '@angular/common/http';
import {UserRole} from '../../domain/entities/auth-user';
import {Observable} from 'rxjs';

export interface AuthenticationServicePort {
  http: HttpClient;
  localStorage: Storage;

  login(email: string, password: string, role: UserRole): Observable<boolean>;

  logout(): void;

  getCurrentUser(): any;

  passwordResetRequest(email: string): Observable<boolean>;

  changePassword(email: string, currentPwd: string | null | undefined, newPwd: string | null | undefined): Observable<boolean>;

  passwordReset(email: string, token: string, password: string, confirmPassword: string): any;
}
