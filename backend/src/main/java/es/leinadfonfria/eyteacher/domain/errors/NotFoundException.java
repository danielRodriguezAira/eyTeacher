package es.leinadfonfria.eyteacher.domain.errors;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException{
    private final Integer code;
    public NotFoundException(String message, Integer code) {
        super(message);
        this.code = code;
    }
    public NotFoundException(String message, Throwable cause, Integer code) {
        super(message, cause);
        this.code = code;
    }
}
