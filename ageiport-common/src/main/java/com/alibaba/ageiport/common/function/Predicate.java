package com.alibaba.ageiport.common.function;


/**
 * @author lingyi
 */
@FunctionalInterface
public interface Predicate<T> extends java.util.function.Predicate<T> {

    boolean apply(T input);

    /**
     * Indicates whether another object is equal to this predicate.
     *
     * <p>Most implementations will have no reason to override the behavior of {@link Object#equals}.
     * However, an implementation may also choose to return {@code true} whenever {@code object} is a
     * {@link Predicate} that it considers <i>interchangeable</i> with this one. "Interchangeable"
     * <i>typically</i> means that {@code this.apply(t) == that.apply(t)} for all {@code t} of type
     * {@code T}). Note that a {@code false} result from this method does not imply that the
     * predicates are known <i>not</i> to be interchangeable.
     */
    @Override
    boolean equals(Object object);

    @Override
    default boolean test(T input) {
        return apply(input);
    }
}
