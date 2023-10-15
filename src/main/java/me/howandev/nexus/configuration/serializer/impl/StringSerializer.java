package me.howandev.nexus.configuration.serializer.impl;

import me.howandev.nexus.configuration.Serializer;
import me.howandev.nexus.configuration.serializer.SerializerRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;

public class StringSerializer implements Serializer<String> {
    @Override
    public @NotNull String getAlias() {
        return "String";
    }

    @Override
    public @NotNull Object serialize(final @NotNull Object value) throws IllegalArgumentException {
        return String.valueOf(value);
    }

    @Override
    public @NotNull Object deserialize(final @NotNull Object value) throws IllegalArgumentException {
        if (value instanceof String stringValue) {
            //TODO: implement proper handling (just testing)
            if (stringValue.endsWith("f") || stringValue.endsWith("F")) {
                try {
                    return Float.parseFloat(stringValue.substring(0, stringValue.length() - 1));
                } catch (NumberFormatException ignored) { }
            }

            if (stringValue.endsWith("d") || stringValue.endsWith("D")) {
                try {
                    return Double.parseDouble(stringValue.substring(0, stringValue.length() - 1));
                } catch (NumberFormatException ignored) { }
            }

            if (stringValue.endsWith("l") || stringValue.endsWith("L")) {
                try {
                    return Long.parseLong(stringValue.substring(0, stringValue.length() - 1));
                } catch (NumberFormatException ignored) { }
            }

            Matcher matcher = FORCE_SERIALIZER_PATTERN.matcher(stringValue);
            if (matcher.matches()) {
                Serializer<?> serializer = SerializerRegistry.serializerFor(matcher.group(1));
                if (serializer != null) return serializer.deserialize(matcher.group(2));
            }
        }

        return value;
    }

    @Override
    public boolean canHandle(final @NotNull Object value) {
        return true;
    }

    @Override
    public @Nullable String parse(final @Nullable Object value, final @Nullable String defaultValue) {
        if (value == null) return defaultValue;

        return String.valueOf(value);
    }
}
