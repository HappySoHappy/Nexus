package me.howandev.nexus.configuration.serializer.impl;

import me.howandev.nexus.configuration.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ByteSerializer implements Serializer<Byte> {
    @Override
    public @NotNull String getAlias() {
        return "Byte";
    }

    @Override
    public @NotNull Object serialize(final @NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof Number number)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        return String.valueOf(number.byteValue());
    }

    @Override
    public @NotNull Object deserialize(final @NotNull Object value) throws IllegalArgumentException {
        if (value instanceof Number numberValue)
            return numberValue.byteValue();

        if (value instanceof String stringValue) {
            try {
                return Byte.parseByte(stringValue);
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
    public @Nullable Byte parse(final @Nullable Object value, final @Nullable Byte defaultValue) {
        if (value instanceof Number numberValue)
            return numberValue.byteValue();

        if (value instanceof String stringValue) {
            try {
                return Byte.parseByte(stringValue);
            } catch (NumberFormatException ignored) { }
        }

        return defaultValue;
    }
}
