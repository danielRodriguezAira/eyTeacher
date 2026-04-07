import {AuthError, DomainError, UnauthorizedError} from './auth.errors';

describe('DomainError', () => {
    it('is abstract and cannot be instantiated directly — subclasses extend it', () => {
        expect(UnauthorizedError.prototype).toBeInstanceOf(DomainError);
        expect(AuthError.prototype).toBeInstanceOf(DomainError);
    });

    it('subclasses are instances of Error', () => {
        const err = new UnauthorizedError();
        expect(err).toBeInstanceOf(Error);
    });
});

describe('UnauthorizedError', () => {
    it('uses default message when none is provided', () => {
        const err = new UnauthorizedError();
        expect(err.message).toBe('No autorizado');
    });

    it('uses custom message when provided', () => {
        const err = new UnauthorizedError('Acceso denegado');
        expect(err.message).toBe('Acceso denegado');
    });

    it('sets name to "UnauthorizedError"', () => {
        const err = new UnauthorizedError();
        expect(err.name).toBe('UnauthorizedError');
    });

    it('is an instance of DomainError', () => {
        const err = new UnauthorizedError();
        expect(err).toBeInstanceOf(DomainError);
    });

    it('is an instance of Error', () => {
        const err = new UnauthorizedError();
        expect(err).toBeInstanceOf(Error);
    });
});

describe('AuthError', () => {
    it('uses default message when none is provided', () => {
        const err = new AuthError();
        expect(err.message).toBe('Error durante autenticación');
    });

    it('uses custom message when provided', () => {
        const err = new AuthError('Token expirado');
        expect(err.message).toBe('Token expirado');
    });

    it('sets name to "AuthError"', () => {
        const err = new AuthError();
        expect(err.name).toBe('AuthError');
    });

    it('is an instance of DomainError', () => {
        const err = new AuthError();
        expect(err).toBeInstanceOf(DomainError);
    });

    it('is an instance of Error', () => {
        const err = new AuthError();
        expect(err).toBeInstanceOf(Error);
    });
});
