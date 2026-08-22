package example.PopularityCounterLLD;

import java.util.*;

public class PopularityCounterV2<T> {

    private final Map<T, Integer> itemToCount = new HashMap<>();

    private final TreeMap<Integer, Set<T>> countToItems =
            new TreeMap<>();

    public void increase(T item) {

        int oldCount = itemToCount.getOrDefault(item, 0);
        int newCount = oldCount + 1;

        // Remove from old bucket
        if (oldCount > 0) {
            Set<T> items = countToItems.get(oldCount);
            items.remove(item);

            if (items.isEmpty()) {
                countToItems.remove(oldCount);
            }
        }

        // Add to new bucket
        itemToCount.put(item, newCount);

        countToItems
                .computeIfAbsent(newCount, k -> new HashSet<>())
                .add(item);
    }

    public void decrease(T item) {

        Integer oldCount = itemToCount.get(item);

        if (oldCount == null) {
            return;
        }

        int newCount = oldCount - 1;

        // Remove from old bucket
        Set<T> items = countToItems.get(oldCount);
        items.remove(item);

        if (items.isEmpty()) {
            countToItems.remove(oldCount);
        }

        // Remove item completely
        if (newCount == 0) {
            itemToCount.remove(item);
            return;
        }

        // Add to new bucket
        itemToCount.put(item, newCount);

        countToItems
                .computeIfAbsent(newCount, k -> new HashSet<>())
                .add(item);
    }

    public int getCount(T item) {
        return itemToCount.getOrDefault(item, 0);
    }

    public T getMostPopular() {

        if (countToItems.isEmpty()) {
            return null;
        }

        return countToItems.lastEntry()
                .getValue()
                .iterator()
                .next();
    }
}