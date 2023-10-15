package me.howandev.nexus.configuration.serializer.impl;

import me.howandev.nexus.configuration.Configuration;
import me.howandev.nexus.configuration.impl.MemoryConfiguration;
import me.howandev.nexus.configuration.Serializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ConfigurationSerializer implements Serializer<Configuration> {
    @Override
    public @NotNull String getAlias() {
        return "Configuration";
    }

    @Override
    public @NotNull Map<String, Object> serialize(final @NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof Configuration configuration)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        return configuration.getValues(false);
    }

    @Override
    public @NotNull Object deserialize(final @NotNull Object value) throws IllegalArgumentException {
        if (value instanceof Configuration configuration) {
            return configuration;
        }

        if (value instanceof Map<?, ?> map) {
            Configuration configuration = new MemoryConfiguration();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                configuration.set(entry.getKey().toString(), entry.getValue());
            }

            return configuration;
        }

        throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");
    }

    @Override
    public boolean canHandle(final @NotNull Object value) {
        if (value instanceof Class<?> clazz) {
            return (Configuration.class.isAssignableFrom(clazz));
        }

        return (value instanceof Map<?,?> || value instanceof Configuration);
    }

    @Override
    public @Nullable Configuration parse(final @Nullable Object value, final @Nullable Configuration defaultValue) {
        if (value instanceof Map<?, ?> map) {
            Configuration configuration = new MemoryConfiguration();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                configuration.set(entry.getKey().toString(), entry.getValue());
            }

            return configuration;
        }

        if (value instanceof Configuration configuration) {
            return configuration;
        }

        return defaultValue;
    }
}
