package es.leinadfonfria.eyteacher.domain.errors;

public class UserException extends RuntimeException{
    public UserException(String message) {
        super(message);
    }
}
