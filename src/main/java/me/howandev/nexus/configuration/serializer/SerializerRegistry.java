package me.howandev.nexus.configuration.serializer;

import me.howandev.nexus.configuration.Configuration;
import me.howandev.nexus.configuration.Serializer;
import me.howandev.nexus.configuration.serializer.impl.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class SerializerRegistry {
    private static final Map<Class<?>, Serializer<?>> REGISTERED_SERIALIZERS = new HashMap<>();
    public static final BooleanSerializer BOOLEAN_SERIALIZER;
    public static final ByteSerializer BYTE_SERIALIZER;
    public static final CharacterSerializer CHARACTER_SERIALIZER;
    public static final ConfigurationSerializer CONFIGURATION_SERIALIZER;
    public static final DoubleSerializer DOUBLE_SERIALIZER;
    public static final FloatSerializer FLOAT_SERIALIZER;
    public static final IntegerSerializer INTEGER_SERIALIZER;
    public static final ListSerializer LIST_SERIALIZER;
    public static final LongSerializer LONG_SERIALIZER;
    public static final MapSerializer MAP_SERIALIZER;
    public static final ShortSerializer SHORT_SERIALIZER;
    public static final StringSerializer STRING_SERIALIZER;

    static {
        BOOLEAN_SERIALIZER = new BooleanSerializer();
        register(Boolean.class, BOOLEAN_SERIALIZER);

        BYTE_SERIALIZER = new ByteSerializer();
        register(Byte.class, BYTE_SERIALIZER);

        CHARACTER_SERIALIZER = new CharacterSerializer();
        register(Character.class, CHARACTER_SERIALIZER);

        CONFIGURATION_SERIALIZER = new ConfigurationSerializer();
        register(Configuration.class, CONFIGURATION_SERIALIZER);

        DOUBLE_SERIALIZER = new DoubleSerializer();
        register(Double.class, DOUBLE_SERIALIZER);

        FLOAT_SERIALIZER = new FloatSerializer();
        register(Float.class, FLOAT_SERIALIZER);

        INTEGER_SERIALIZER = new IntegerSerializer();
        register(Integer.class, INTEGER_SERIALIZER);

        LIST_SERIALIZER = new ListSerializer();
        register(List.class, LIST_SERIALIZER);

        LONG_SERIALIZER = new LongSerializer();
        register(Long.class, LONG_SERIALIZER);

        MAP_SERIALIZER = new MapSerializer();
        register(Map.class, MAP_SERIALIZER);

        SHORT_SERIALIZER = new ShortSerializer();
        register(Short.class, SHORT_SERIALIZER);

        STRING_SERIALIZER = new StringSerializer();
        register(String.class, STRING_SERIALIZER);
    }

    public static void register(final @NotNull Class<?> clazz, final @NotNull Serializer<?> serializer) {
        REGISTERED_SERIALIZERS.put(clazz, serializer);
    }

    public static @Nullable Serializer<?> serializerFor(final @NotNull Object object) {
        if (object instanceof Class<?> clazz) {
            Serializer<?> serializer = REGISTERED_SERIALIZERS.get(clazz);
            if (serializer != null) return serializer;

            for (Class<?> interfaceClass : clazz.getInterfaces()) {
                serializer = serializerFor(interfaceClass);
                if (serializer != null) return serializer;
            }

            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) return serializerFor(superclass);

            return null;
        }

        if (object instanceof String stringValue) {
            try {
                for (Map.Entry<Class<?>, Serializer<?>> entry : REGISTERED_SERIALIZERS.entrySet()) {
                    if (entry.getValue().getAlias().equalsIgnoreCase(stringValue))
                        return entry.getValue();
                }

                Class<?> clazz = Class.forName(stringValue);
                return serializerFor(clazz);
            } catch (ClassNotFoundException ignored) { }
        }

        return serializerFor(object.getClass());
    }
}
