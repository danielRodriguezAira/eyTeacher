export abstract class DomainError extends Error {
  protected constructor(message: string) {
    super(message);
    this.name = this.constructor.name;
  }
}

export class UnauthorizedError extends DomainError {
  constructor(message: string = 'No autorizado') {
    super(message);
  }
}

export class AuthError extends DomainError {
  constructor(message: string = 'Error durante autenticación') {
    super(message);
  }
}
