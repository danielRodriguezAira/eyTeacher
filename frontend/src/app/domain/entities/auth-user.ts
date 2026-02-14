export enum UserRole {
  TEACHER = 'Profesor',
  STUDENT = 'Alumno'
}

export interface AuthUser {
  token: string;
  isAdmin: boolean;
  email: string;
  id: string;
  expiration: string;
  firstName: string;
  lastName: string;
  role: UserRole;
}
