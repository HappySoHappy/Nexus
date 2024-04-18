package me.howandev.nexus.configuration.serializer.impl;

import me.howandev.nexus.configuration.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BooleanSerializer implements Serializer<Boolean> {
    @Override
    public @NotNull String getName() {
        return "Boolean";
    }

    @Override
    public @NotNull Object serialize(final @NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof Boolean bool)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        return String.valueOf(bool);
    }

    @Override
    public @NotNull Object deserialize(final @NotNull Object value) throws IllegalArgumentException {
        if (value instanceof Boolean boolValue)
            return boolValue;

        if (value instanceof String stringValue) {
            if (stringValue.equalsIgnoreCase("true"))
                return true;

            if (stringValue.equalsIgnoreCase("false"))
                return false;
        }

        throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");
    }

    @Override
    public boolean canHandle(final @NotNull Object value) {
        if (value instanceof Class<?> clazz) {
            return (Boolean.class.isAssignableFrom(clazz) || clazz == String.class);
        }

        return (value instanceof Boolean || value instanceof String);
    }

    @Override
    public @Nullable Boolean parse(final @Nullable Object value, final @Nullable Boolean defaultValue) {
        if (value instanceof Boolean boolValue)
            return boolValue;

        if (value instanceof String stringValue) {
            if (stringValue.equalsIgnoreCase("true"))
                return true;

            if (stringValue.equalsIgnoreCase("false"))
                return false;
        }

        return defaultValue;
    }
}
