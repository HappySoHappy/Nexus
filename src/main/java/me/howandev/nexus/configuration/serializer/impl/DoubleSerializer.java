package me.howandev.nexus.configuration.serializer.impl;

import me.howandev.nexus.configuration.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DoubleSerializer implements Serializer<Double> {
    @Override
    public @NotNull String getName() {
        return "Double";
    }

    @Override
    public @NotNull Object serialize(final @NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof Number number)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        return String.valueOf(number.doubleValue());
    }

    @Override
    public @NotNull Object deserialize(final @NotNull Object value) throws IllegalArgumentException {
        if (value instanceof Number numberValue)
            return numberValue.doubleValue();

        if (value instanceof String stringValue) {
            try {
                return Double.parseDouble(stringValue);
            } catch (NumberFormatException ignored) { }
        }

        throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");
    }

    @Override
    public boolean canHandle(final @NotNull Object value) {
        if (value instanceof Class<?> clazz) {
            return (Number.class.isAssignableFrom(clazz) || clazz == String.class);
        }

        return (value instanceof Number || value instanceof String);
    }

    @Override
    public @Nullable Double parse(final @Nullable Object value, final @Nullable Double defaultValue) {
        if (value instanceof Number numberValue)
            return numberValue.doubleValue();

        if (value instanceof String stringValue) {
            try {
                return Double.parseDouble(stringValue);
            } catch (NumberFormatException ignored) { }
        }

        return defaultValue;
    }
}
