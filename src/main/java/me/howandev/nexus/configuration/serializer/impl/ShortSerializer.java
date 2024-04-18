package me.howandev.nexus.configuration.serializer.impl;

import me.howandev.nexus.configuration.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShortSerializer implements Serializer<Short> {
    @Override
    public @NotNull String getName() {
        return "Short";
    }

    @Override
    public @NotNull Object serialize(final @NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof Number number)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        return String.valueOf(number.shortValue());
    }

    @Override
    public @NotNull Object deserialize(final @NotNull Object value) throws IllegalArgumentException {
        if (value instanceof Number numberValue)
            return numberValue.shortValue();

        if (value instanceof String stringValue) {
            try {
                return Short.parseShort(stringValue);
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
    public @Nullable Short parse(final @Nullable Object value, final @Nullable Short defaultValue) {
        if (value instanceof Number numberValue)
            return numberValue.shortValue();

        if (value instanceof String stringValue) {
            try {
                return Short.parseShort(stringValue);
            } catch (NumberFormatException ignored) { }
        }

        return defaultValue;
    }
}
