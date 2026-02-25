package es.leinadfonfria.eyteacher.domain.errors;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException{
    private final Integer code;
    public AuthException(String message, Integer code) {
        super(message);
        this.code = code;
    }
    public AuthException(String message, Throwable cause, Integer code) {
        super(message, cause);
        this.code = code;
    }
}
