package example.Epam_oops_stream_java8;

import java.util.Optional;

@FunctionalInterface
public interface Transformer<T, R> {
    Optional<R> transform(T input);

    // Chain this transformer with another
    default <V> Transformer<T, V> andThen(Transformer<R, V> next) {
        return input -> this.transform(input).flatMap(next::transform);
    }


}
