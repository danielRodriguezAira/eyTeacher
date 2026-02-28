const ErrorCodeMessages: Record<number, string> = {
    1000: 'Ocurrió un error inesperado o no clasificado.',
    1001: 'No se pudo encontrar el usuario solicitado.',
    1002: 'El formato del ID de usuario no es válido.',
    1003: 'Los datos del usuario son inválidos o están incompletos.',
    1004: 'El correo electrónico ya está registrado.',
    1005: 'La contraseña no cumple con los criterios requeridos.',
    1006: 'Las contraseñas no coinciden.',
    1007: 'Las credenciales de inicio de sesión son incorrectas.'
};

export const getErrorMessage = (code: number, fallback: string = ErrorCodeMessages[1000]) =>
    ErrorCodeMessages[code] ?? fallback;
