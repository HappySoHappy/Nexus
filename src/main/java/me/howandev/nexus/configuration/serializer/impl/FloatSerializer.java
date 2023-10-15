package me.howandev.nexus.configuration.serializer.impl;

import me.howandev.nexus.configuration.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FloatSerializer implements Serializer<Float> {
    @Override
    public @NotNull String getAlias() {
        return "Float";
    }

    @Override
    public @NotNull Object serialize(final @NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof Number number)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        return String.valueOf(number.floatValue());
    }

    @Override
    public @NotNull Object deserialize(final @NotNull Object value) throws IllegalArgumentException {
        if (value instanceof Number numberValue)
            return numberValue.floatValue();

        if (value instanceof String stringValue) {
            try {
                return Float.parseFloat(stringValue);
            } catch (NumberFormatException ignored) { }
        }

        throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");
    }

    @Override
    public boolean canHandle(final @NotNull Object value) {
        if (value instanceof Class<?> clazz) {
            return (Number.class.isAssignableFrom(clazz));
        }

        return (value instanceof Number || value instanceof String);
    }

    @Override
    public @Nullable Float parse(final @Nullable Object value, final @Nullable Float defaultValue) {
        if (value instanceof Number numberValue)
            return numberValue.floatValue();

        if (value instanceof String stringValue) {
            try {
                return Float.parseFloat(stringValue);
            } catch (NumberFormatException ignored) { }
        }

        return defaultValue;
    }
}
