import {getErrorMessage} from './error-codes';

describe('getErrorMessage', () => {
    describe('known error codes', () => {
        it('returns message for 1000 (UNKNOWN_ERROR)', () => {
            expect(getErrorMessage(1000)).toBe('Ocurrió un error inesperado o no clasificado.');
        });

        it('returns message for 1001 (USER_NOT_FOUND)', () => {
            expect(getErrorMessage(1001)).toBe('No se pudo encontrar el usuario solicitado.');
        });

        it('returns message for 1002 (INVALID_USER_ID_FORMAT)', () => {
            expect(getErrorMessage(1002)).toBe('El formato del ID de usuario no es válido.');
        });

        it('returns message for 1003 (INVALID_USER_DATA)', () => {
            expect(getErrorMessage(1003)).toBe('Los datos del usuario son inválidos o están incompletos.');
        });

        it('returns message for 1004 (EMAIL_ALREADY_IN_USE)', () => {
            expect(getErrorMessage(1004)).toBe('El correo electrónico ya está registrado.');
        });

        it('returns message for 1005 (INVALID_PASSWORD)', () => {
            expect(getErrorMessage(1005)).toBe('La contraseña no cumple con los criterios requeridos.');
        });

        it('returns message for 1006 (DIFFERENT_PASSWORD)', () => {
            expect(getErrorMessage(1006)).toBe('Las contraseñas no coinciden.');
        });

        it('returns message for 1007 (INVALID_CREDENTIALS)', () => {
            expect(getErrorMessage(1007)).toBe('Las credenciales de inicio de sesión son incorrectas.');
        });

        it('returns message for 1008 (AUTHENTICATION_ERROR)', () => {
            expect(getErrorMessage(1008)).toBe('Error de autenticación.');
        });

        it('returns message for 2001 (CATEGORY_USER_NOT_FOUND)', () => {
            expect(getErrorMessage(2001)).toBe('No se encontró el propietario de la categoría.');
        });

        it('returns message for 2002 (USER_NOT_TEACHER)', () => {
            expect(getErrorMessage(2002)).toBe('El usuario no tiene rol de profesor.');
        });

        it('returns message for 2003 (CATEGORY_NOT_FOUND)', () => {
            expect(getErrorMessage(2003)).toBe('No se encontró la categoría.');
        });

        it('returns message for 2004 (USER_NOT_STUDENT)', () => {
            expect(getErrorMessage(2004)).toBe('El usuario no tiene rol de alumno.');
        });

        it('returns message for 2005 (CATEGORY_HAS_TOPICS)', () => {
            expect(getErrorMessage(2005)).toBe('No se puede eliminar la categoría porque tiene temas asociados.');
        });

        it('returns message for 3002 (TOPIC_OWNER_NOT_TEACHER)', () => {
            expect(getErrorMessage(3002)).toBe('El propietario del tema no tiene rol de profesor.');
        });

        it('returns message for 3003 (TOPIC_NOT_FOUND)', () => {
            expect(getErrorMessage(3003)).toBe('No se encontró el tema.');
        });

        it('returns message for 3004 (TOPIC_CATEGORY_NOT_FOUND)', () => {
            expect(getErrorMessage(3004)).toBe('No se encontró la categoría del tema.');
        });

        it('returns message for 3005 (TOPIC_INVALID_DATA)', () => {
            expect(getErrorMessage(3005)).toBe('Los datos del tema no son válidos.');
        });

        it('returns message for 3006 (TOPIC_HAS_TASKS)', () => {
            expect(getErrorMessage(3006)).toBe('No se puede eliminar el tema porque tiene tareas asociadas.');
        });

        it('returns message for 4001 (TASK_NOT_FOUND)', () => {
            expect(getErrorMessage(4001)).toBe('No se encontró la tarea.');
        });

        it('returns message for 5001 (SOLUTION_NOT_FOUND)', () => {
            expect(getErrorMessage(5001)).toBe('No se encontró la solución.');
        });

        it('returns message for 6001 (CORRECTION_NOT_FOUND)', () => {
            expect(getErrorMessage(6001)).toBe('No se encontró la corrección.');
        });

        it('returns message for 7001 (NOTIFICATION_NOT_FOUND)', () => {
            expect(getErrorMessage(7001)).toBe('No se encontró la notificación.');
        });
    });

    describe('unknown error codes', () => {
        it('returns default fallback (code 1000 message) when code is unknown and no fallback provided', () => {
            expect(getErrorMessage(9999)).toBe('Ocurrió un error inesperado o no clasificado.');
        });

        it('returns custom fallback when code is unknown and fallback is provided', () => {
            expect(getErrorMessage(9999, 'Error personalizado')).toBe('Error personalizado');
        });

        it('returns custom fallback for code 0', () => {
            expect(getErrorMessage(0, 'Error de fallback')).toBe('Error de fallback');
        });

        it('returns custom fallback for negative code', () => {
            expect(getErrorMessage(-1, 'Código negativo')).toBe('Código negativo');
        });
    });

    describe('fallback parameter', () => {
        it('ignores fallback when code is known', () => {
            expect(getErrorMessage(1001, 'ignorado')).toBe('No se pudo encontrar el usuario solicitado.');
        });

        it('uses fallback when code is unknown', () => {
            expect(getErrorMessage(8888, 'usando fallback')).toBe('usando fallback');
        });
    });
});
