package com.epita.exchange.utils;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.stream.Collectors;

public interface Converter<T, U> {
    default U convert(final T input) {
        if (input == null) {
            return null;
        }
        return convertNotNull(input);
    }

    @NotNull
    U convertNotNull(@NotNull final T input);

    default List<U> convertList(final List<T> inputList) {
        return inputList.stream().map(this::convert).collect(Collectors.toList());
    }
}
