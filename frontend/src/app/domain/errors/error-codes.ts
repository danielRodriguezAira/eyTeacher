const ErrorCodeMessages: Record<number, string> = {
    1000: 'Ocurrió un error inesperado o no clasificado.',
    1001: 'No se pudo encontrar el usuario solicitado.',
    1002: 'El formato del ID de usuario no es válido.',
    1003: 'Los datos del usuario son inválidos o están incompletos.',
    1004: 'El correo electrónico ya está registrado.',
    1005: 'La contraseña no cumple con los criterios requeridos.',
    1006: 'Las contraseñas no coinciden.',
    1007: 'Las credenciales de inicio de sesión son incorrectas.',
    1008: 'Error de autenticación.',
    2001: 'No se encontró el propietario de la categoría.',
    2002: 'El usuario no tiene rol de profesor.',
    2003: 'No se encontró la categoría.',
    2004: 'El usuario no tiene rol de alumno.',
    2005: 'No se puede eliminar la categoría porque tiene temas asociados.',
    3002: 'El propietario del tema no tiene rol de profesor.',
    3003: 'No se encontró el tema.',
    3004: 'No se encontró la categoría del tema.',
    3005: 'Los datos del tema no son válidos.',
    3006: 'No se puede eliminar el tema porque tiene tareas asociadas.',
    4001: 'No se encontró la tarea.',
    5001: 'No se encontró la solución.',
    6001: 'No se encontró la corrección.',
    7001: 'No se encontró la notificación.',
};

export const getErrorMessage = (code: number, fallback: string = ErrorCodeMessages[1000]) =>
    ErrorCodeMessages[code] ?? fallback;
