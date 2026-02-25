package es.leinadfonfria.eyteacher.application.shared;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Implementación del patrón Result en Java.
 * Utiliza 'sealed' para garantizar que solo existan los casos Success y Failure.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Result.Success.class),
        @JsonSubTypes.Type(value = Result.Failure.class)
})
public sealed interface Result<T, E> permits Result.Success, Result.Failure {

    // --- Métodos de Fábrica ---

    static <T, E> Result<T, E> ok(T value) {
        return new Success<>(value);
    }

    static <T, E> Result<T, E> fail(E error) {
        return new Failure<>(error);
    }

    // --- Métodos de Estado ---

    boolean isSuccess();

    default boolean isFailure() {
        return !isSuccess();
    }

    // --- Implementaciones de los Tipos ---

    record Success<T, E>(T value) implements Result<T, E> {
        @Override public boolean isSuccess() { return true; }
        @JsonIgnore
        public T data() { return value; }
    }

    record Failure<T, E>(E error) implements Result<T, E> {
        @Override public boolean isSuccess() { return false; }
    }

    // --- Operaciones Funcionales (map, flatMap, fold, combine) ---
    
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

    @SuppressWarnings("unchecked")
    default <U> Result<U, E> flatMap(Function<? super T, Result<U, E>> fn) {
        if (this instanceof Success<T, E>(T value)) {
            return fn.apply(value);
        }
        return (Result<U, E>) this;
    }

    /**
     * Combina una lista de resultados en un resultado de lista.
     * Si uno falla, devuelve el primer error encontrado.
     */
    static <T, E> Result<List<T>, E> combine(List<Result<T, E>> results) {
        List<T> values = new ArrayList<>(results.size());
        for (var result : results) {
            if (result instanceof Success<T, E>(T value)) {
                values.add(value);
            } else if (result instanceof Failure<T, E>(E error)) {
                return Result.fail(error);
            }
        }
        return Result.ok(values);
    }

    // Métodos de utilidad para acceder al valor o error de forma segura con Pattern Matching
    @JsonIgnore
    default T getValue() {
        if (this instanceof Success<T, E>(T value)) return value;
        throw new IllegalStateException("No se puede obtener el valor de un Result.Failure");
    }

    @JsonIgnore
    default E getError() {
        if (this instanceof Failure<T, E>(E error)) return error;
        throw new IllegalStateException("No se puede obtener el error de un Result.Success");
    }
}
