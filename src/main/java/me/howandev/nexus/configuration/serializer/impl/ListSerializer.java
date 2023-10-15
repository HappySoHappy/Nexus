package me.howandev.nexus.configuration.serializer.impl;

import me.howandev.nexus.configuration.Serializer;
import me.howandev.nexus.configuration.serializer.SerializerRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ListSerializer implements Serializer<List<?>> {
    @Override
    public @NotNull String getAlias() {
        return "List";
    }

    @Override
    public @NotNull List<?> serialize(final @NotNull Object value) {
        if (!(value instanceof List<?> list)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        List<Object> serializedList = new ArrayList<>(list.size());
        for (Object element : list) {
            if (element == null) {
                serializedList.add(null);
                continue;
            }

            Serializer<?> serializer = SerializerRegistry.serializerFor(element.getClass());
            if (serializer != null) {
                serializedList.add(serializer.serialize(element));
            }
        }

        return serializedList;
    }

    @Override
    public @NotNull Object deserialize(final @NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof List<?> list)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        List<Object> deserializedList = new ArrayList<>();
        for (Object element : list) {
            Serializer<?> serializer = SerializerRegistry.serializerFor(element);
            if (serializer != null)
                deserializedList.add(serializer.deserialize(element));
        }

        return deserializedList;
    }

    @Override
    public boolean canHandle(final @NotNull Object value) {
        return value instanceof List;
    }

    @Override
    public @Nullable List<?> parse(final Object serialized, final List<?> defaultValue) {
        if (serialized instanceof List<?> serializedList) {
            List<Object> deserializedList = new ArrayList<>();
            for (Object element : serializedList) {
                if (element == null) {
                    deserializedList.add(null);
                    continue;
                }

                Serializer<?> serializer = SerializerRegistry.serializerFor(element);
                if (serializer != null)
                    deserializedList.add(serializer.parse(element));

            }
            return deserializedList;
        }

        return defaultValue;
    }
}
