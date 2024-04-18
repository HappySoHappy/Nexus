package me.howandev.nexus.configuration.serializer.impl;

import me.howandev.nexus.configuration.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CharacterSerializer implements Serializer<Character> {
    @Override
    public @NotNull String getName() {
        return "Char";
    }

    @Override
    public @NotNull Object serialize(final @NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof Character character)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        return String.valueOf(character);
    }

    @Override
    public @NotNull Object deserialize(final @NotNull Object value) throws IllegalArgumentException {
        if (value instanceof Character charValue)
            return charValue;

        if (value instanceof String && !((String) value).isEmpty()) {
            try {
                return ((String) value).charAt(0);
            } catch (NumberFormatException ignored) { }
        }

        throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");
    }

    @Override
    public boolean canHandle(final @NotNull Object value) {
        if (value instanceof Class<?> clazz) {
            return (Character.class.isAssignableFrom(clazz) || clazz == String.class);
        }

        return (value instanceof Character || value instanceof String);
    }

    @Override
    public @Nullable Character parse(final @Nullable Object value, final @Nullable Character defaultValue) {
        if (value instanceof Character charValue)
            return charValue;

        if (value instanceof String && !((String) value).isEmpty()) {
            try {
                return ((String) value).charAt(0);
            } catch (NumberFormatException ignored) { }
        }

        return defaultValue;
    }
}
