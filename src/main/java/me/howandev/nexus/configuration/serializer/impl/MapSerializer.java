package me.howandev.nexus.configuration.serializer.impl;

import me.howandev.nexus.configuration.Configuration;
import me.howandev.nexus.configuration.Serializer;
import me.howandev.nexus.configuration.serializer.SerializerRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapSerializer implements Serializer<Map<?, ?>> {
    @Override
    public @NotNull String getAlias() {
        return "Map";
    }

    /*
    @Override
    public @NotNull Map<?, ?> serialize(final @NotNull Object value) {
        if (!(value instanceof Map<?, ?> map)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        return map;
    }
    */

    //TODO: this method needs a lot of testing, on both YAML and JSON - see method above.
    @Override
    public @NotNull Map<?, ?> serialize(final @NotNull Object value) {
        if (value instanceof Configuration configuration) {
            return configuration.getValues(false);
        }

        if (!(value instanceof Map<?, ?> map)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");
        Map<String, Object> serializedMap = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (entry.getValue() == null) {
                serializedMap.put(entry.getKey().toString(), null);
                continue;
            }

            Serializer<?> serializer = SerializerRegistry.serializerFor(entry.getValue());
            if (serializer != null) {
                serializedMap.put(entry.getKey().toString(), serializer.serialize(entry.getValue()));
            }
        }

        return serializedMap;
    }


    @Override
    public @NotNull Object deserialize(final @NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof Map<?, ?> map)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        Map<Object, Object> deserializedMap = new LinkedHashMap<>();
        if (map.containsKey(Serializer.SERIALIZED_OBJECT_KEY)) {
            String name = map.get(Serializer.SERIALIZED_OBJECT_KEY).toString();
            Serializer<?> customSerializer = SerializerRegistry.serializerFor(name);
            try {
                if (customSerializer == null)
                    customSerializer = SerializerRegistry.serializerFor(Class.forName(name));

                if (customSerializer != null)
                    return customSerializer.deserialize(map);
            } catch (ClassNotFoundException ignored) { }
        }

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object element = entry.getValue();
            Serializer<?> serializer = SerializerRegistry.serializerFor(element);
            if (serializer != null)
                deserializedMap.put(entry.getKey(), serializer.deserialize(element));
        }

        return deserializedMap;
    }

    @Override
    public boolean canHandle(final @NotNull Object value) {
        return value instanceof Map;
    }

    @Override
    public @Nullable Map<?, ?> parse(final Object serialized, final Map<?, ?> defaultValue) {
        if (serialized instanceof Configuration configuration) {
            return configuration.getValues(false);
        }

        if (serialized instanceof Map<?,?> serializedMap) {
            Map<Object, Object> deserializedMap = new LinkedHashMap<>(); //Keep order
            for (Map.Entry<?, ?> entry : serializedMap.entrySet()) {
                Object element = entry.getValue();
                Serializer<?> serializer = SerializerRegistry.serializerFor(element.getClass());
                if (serializer != null)
                    deserializedMap.put(entry.getKey(), serializer.parse(element));
            }

            return deserializedMap;
        }

        return defaultValue;
    }
}
