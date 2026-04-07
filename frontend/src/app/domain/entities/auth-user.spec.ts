import {AuthUser, UserRole} from './auth-user';

describe('UserRole', () => {
    it('TEACHER has value "Profesor"', () => {
        expect(UserRole.TEACHER).toBe('Profesor');
    });

    it('STUDENT has value "Alumno"', () => {
        expect(UserRole.STUDENT).toBe('Alumno');
    });

    it('contains exactly two roles', () => {
        const roles = Object.values(UserRole);
        expect(roles).toHaveLength(2);
        expect(roles).toContain('Profesor');
        expect(roles).toContain('Alumno');
    });
});

describe('AuthUser', () => {
    it('can be created with all required fields', () => {
        const user: AuthUser = {
            token: 'jwt-token',
            email: 'test@example.com',
            id: 'user-123',
            expiration: '2026-12-31',
            firstName: 'Ana',
            lastName: 'García',
            role: UserRole.TEACHER,
        };

        expect(user.token).toBe('jwt-token');
        expect(user.email).toBe('test@example.com');
        expect(user.id).toBe('user-123');
        expect(user.expiration).toBe('2026-12-31');
        expect(user.firstName).toBe('Ana');
        expect(user.lastName).toBe('García');
        expect(user.role).toBe(UserRole.TEACHER);
    });

    it('accepts STUDENT role', () => {
        const user: AuthUser = {
            token: 'token',
            email: 'student@example.com',
            id: 'student-1',
            expiration: '2026-12-31',
            firstName: 'Luis',
            lastName: 'Pérez',
            role: UserRole.STUDENT,
        };

        expect(user.role).toBe(UserRole.STUDENT);
    });
});
