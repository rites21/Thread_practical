package example.Epam_oops_stream_java8;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Main {
    public static <T, R> List<R> applyPipeline(List<T> input, Transformer<T, R> pipeline) {
        List<R> results = new ArrayList<>();
        for (T item : input) {
            pipeline.transform(item).ifPresent(results::add);
        }
        return results;
    }
    public static void main(String[] args) {
        List<String> input = List.of("1", "2", "abc", "4", "5", "10");

        // Step 1: String -> Integer (safe parse)
        Transformer<String, Integer> parseToInt = s -> {
            try {
                return Optional.of(Integer.parseInt(s.trim()));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        };


        // Step 2: Integer -> Integer (keep only even)
        Transformer<Integer, Integer> filterEven = n ->
                (n % 2 == 0) ? Optional.of(n) : Optional.empty();

        // Step 3: Integer -> Integer (multiply by 10)
        Transformer<Integer, Integer> multiplyBy10 = n -> Optional.of(n * 10);

        // Chain them into one pipeline
        Transformer<String, Integer> pipeline = parseToInt.andThen(filterEven).andThen(multiplyBy10);

        List<Integer> results = applyPipeline(input, pipeline);
        System.out.println(results); // [20, 40, 100]
    }
}
