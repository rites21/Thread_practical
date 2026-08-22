package example.PopularityCounterLLD;

import java.util.HashMap;
import java.util.Map;

public class PopularityCounter<T> {
    private final Map<T, Integer> popularity = new HashMap<>();

    public void increase(T item) {
        popularity.merge(item, 1, Integer::sum);
    }

    public void decrease(T item) {
        popularity.computeIfPresent(item, (key, value) -> {
            if (value == 1) {
                return null;
            }
            return value - 1;
        });
    }

    public int getCount(T item) {
        return popularity.getOrDefault(item, 0);
    }

    public T getMostPopular() {

        return popularity.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}
