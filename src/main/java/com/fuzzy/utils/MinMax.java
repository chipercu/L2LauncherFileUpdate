package com.fuzzy.utils;

public class MinMax<T extends Comparable<T>> {

    private T min = null;
    private T max = null;

    public void add(T object) {
        if (min == null || object.compareTo(min) < 0) {
            min = object;
        }
        if (max == null || object.compareTo(max) > 0) {
            max = object;
        }
    }

    public boolean isEmpty() {
        return min == null;
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }
}