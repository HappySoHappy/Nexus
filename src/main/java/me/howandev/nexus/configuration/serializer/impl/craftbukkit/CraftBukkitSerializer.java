package me.howandev.nexus.configuration.serializer.impl.craftbukkit;

import me.howandev.nexus.configuration.Serializer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class CraftBukkitSerializer implements Serializer<ConfigurationSerializable> {
    @Override
    public @NotNull String getName() {
        return "CraftBukkitSerializable";
    }

    @Override
    public @NotNull Map<String, Object> serialize(@NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof ConfigurationSerializable serializable)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        Map<String, Object> map = serializable.serialize(); // need to recursively serialize every field that is instance of Serializble
        map.put(SERIALIZED_OBJECT_KEY, getName());

        return map;
    }

    @Override
    public @NotNull ConfigurationSerializable deserialize(@NotNull Object value) throws IllegalArgumentException {
        if (value instanceof ConfigurationSerializable serializable) return serializable;

        if (value instanceof Map<?, ?> rawMap) {
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                map.put((String)entry.getKey(), entry.getValue());
            }

            return null; //ItemStack.deserialize(map);
        }

        return null; //new ItemStack(Material.AIR);
    }

    @Override
    public boolean canHandle(@NotNull Object value) {
        return (value instanceof ConfigurationSerializable || value instanceof Map<?,?>);
    }

    @Override
    public @Nullable ConfigurationSerializable parse(@Nullable Object value, @Nullable ConfigurationSerializable defaultValue) {
        if (value instanceof ConfigurationSerializable serializable) return serializable;

        if (value instanceof Map<?, ?> rawMap) {
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                map.put((String)entry.getKey(), entry.getValue());
            }

            throw new RuntimeException("unimplemented");
            //return null //ItemStack.deserialize(map);
        }

        return defaultValue;
    }
}
