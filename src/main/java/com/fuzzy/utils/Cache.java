package com.fuzzy.utils;


import com.fuzzy.function.Function;

import java.util.HashMap;
import java.util.Map;

public class Cache<T, Y> {

    private final Map<T, Y> items = new HashMap<>();

    public Y get(T key, Function<T, Y> mappingFunction) {
        Y value;
        if (items.containsKey(key)) {
            value = items.get(key);
        } else {
            value = mappingFunction.apply(key);
            items.put(key, value);
        }
        return value;
    }

    public void clear() {
        items.clear();
    }
}
