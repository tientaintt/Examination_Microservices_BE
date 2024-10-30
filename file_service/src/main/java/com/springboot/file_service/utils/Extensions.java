package com.springboot.file_service.utils;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Extensions {
    public static boolean isNullOrZero(BigDecimal number) {
        return number == null || number.compareTo(BigDecimal.ZERO) == 0;
    }
    public static boolean isBlankOrNull(String str) {
        return str == null || str.isEmpty() || str.isBlank();
    }

    public static <T> boolean isNullOrEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> Stream<T> toStream(Collection<T> collection) {
        return Optional.ofNullable(collection).stream().flatMap(Collection::stream);
    }

    public static <T> List<T> toList(Stream<T> stream) {
        return stream.collect(Collectors.toList());
    }

    public static <T> List<T> toList(Iterable<T> iterator) {
        List<T> result = new ArrayList<>();
        iterator.forEach(result::add);
        return result;
    }

    public static <T> Map<String, T> toMap(Stream<T> stream, Function<? super T, ? extends String> keyMapper) {
        return stream.collect(Collectors.toMap(keyMapper, Function.identity()));
    }

    public static <T> Map<String, T> toMapNoDuplicate(Stream<T> stream, Function<? super T, ? extends String> keyMapper) {
        return stream.collect(Collectors.toMap(keyMapper, Function.identity(), (oldValue, newValue) -> newValue));
    }

    public static <T> Map<String, List<T>> toMapList(Stream<T> stream, Function<? super T, ? extends String> keyMapper) {
        return stream.collect(Collectors.toMap(keyMapper, p -> {
            List<T> list = new ArrayList<>();
            list.add(p);
            return list;
        }, (oldValue, newValue) -> {
            newValue.addAll(oldValue);
            return newValue;
        }));
    }

    public static <T> Collection<T> merge(Collection<T> from, Collection<T> to) {
        if (from == null || from.isEmpty()) {
            from = new ArrayList<>(to);
        } else {
            from.removeIf(t -> !to.contains(t));
        }
        return from;
    }

    public static <T> Optional<T> toOptional(T value) {
        return Optional.ofNullable(value);
    }

}