package es.leinadfonfria.eyteacher.application.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.function.Function;

/**
 * Implementation of the Result pattern in Java.
 * Uses 'sealed' to ensure that only Success and Failure cases exist.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Result.Success.class),
        @JsonSubTypes.Type(value = Result.Failure.class)
})
public sealed interface Result<T, E> permits Result.Success, Result.Failure {

    // --- Factory Methods ---

    static <T, E> Result<T, E> ok(T value) {
        return new Success<>(value);
    }

    static <T, E> Result<T, E> fail(E error) {
        return new Failure<>(error);
    }

    // --- Status Methods ---

    boolean isSuccess();

    default boolean isFailure() {
        return !isSuccess();
    }

    // --- Type Implementations ---

    record Success<T, E>(T value) implements Result<T, E> {
        @Override public boolean isSuccess() { return true; }
        @JsonIgnore
        public T data() { return value; }
    }

    record Failure<T, E>(E error) implements Result<T, E> {
        @Override public boolean isSuccess() { return false; }
    }

    // --- Functional Operations (map, flatMap, fold, combine) ---
    
    default <U> U fold(Function<? super T, ? extends U> successFn, Function<? super E, ? extends U> failureFn) {
        if (this instanceof Success<T, E>(T value)) {
            return successFn.apply(value);
        }
        return failureFn.apply(getError());
    }

    @SuppressWarnings("unchecked")
    default <U> Result<U, E> map(Function<? super T, ? extends U> fn) {
        if (this instanceof Success<T, E>(T value)) {
            return Result.ok(fn.apply(value));
        }
        return (Result<U, E>) this;
    }
    
    // Utility methods for safely accessing value or error with Pattern Matching
    @JsonIgnore
    default T getValue() {
        if (this instanceof Success<T, E>(T value)) return value;
        throw new IllegalStateException("Cannot get value from a Result.Failure");
    }

    @JsonIgnore
    default E getError() {
        if (this instanceof Failure<T, E>(E error)) return error;
        throw new IllegalStateException("Cannot get error from a Result.Success");
    }
}
