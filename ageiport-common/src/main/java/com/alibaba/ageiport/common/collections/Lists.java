package com.alibaba.ageiport.common.collections;


import com.alibaba.ageiport.common.lang.Ints;

import java.util.*;

public final class Lists {
    private Lists() {
    }

    public static <T> List<List<T>> averageAssign(List<T> source, int n) {
        List<List<T>> result = new ArrayList<>();
        int remaider = source.size() % n;
        int number = source.size() / n;
        int offset = 0;
        for (int i = 0; i < n; i++) {
            List<T> value;
            if (remaider > 0) {
                value = source.subList(i * number + offset, (i + 1) * number + offset + 1);
                remaider--;
                offset++;
            } else {
                value = source.subList(i * number + offset, (i + 1) * number + offset);
            }
            result.add(value);
        }
        return result;
    }


    public static <E> ArrayList<E> newArrayList() {
        return new ArrayList<>();
    }

    public static <E> ArrayList<E> newArrayList(E... elements) {
        if (elements == null) {
            throw new IllegalArgumentException("elements is null");
        }
        // Avoid integer overflow when a large array is passed in
        int capacity = computeArrayListCapacity(elements.length);
        ArrayList<E> list = new ArrayList<>(capacity);
        Collections.addAll(list, elements);
        return list;
    }


    public static <E> ArrayList<E> newArrayListWithCapacity(int initialArraySize) {
        return new ArrayList<>(initialArraySize);
    }

    public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
        if (elements == null) {
            throw new IllegalArgumentException("elements is null");
        }
        return (elements instanceof Collection)
                ? new ArrayList<>(cast(elements))
                : newArrayList(elements.iterator());
    }

    public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
        ArrayList<E> list = newArrayList();
        if (elements == null) {
            return list;
        }
        while (elements.hasNext()) {
            list.add(elements.next());
        }
        return list;
    }

    static <T> Collection<T> cast(Iterable<T> iterable) {
        return (Collection<T>) iterable;
    }

    static int computeArrayListCapacity(int arraySize) {
        return Ints.saturatedCast(5L + arraySize + (arraySize / 10));
    }
}
