package me.howandev.nexus.configuration.serializer.impl.craftbukkit;

import me.howandev.nexus.configuration.Serializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ItemStackSerializer implements Serializer<ItemStack> {
    @Override
    public @NotNull String getName() {
        return "ItemStack";
    }

    @Override
    public @NotNull Map<String, Object> serialize(@NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof ItemStack itemStack)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        Map<String, Object> map = itemStack.serialize(); // need to recursively serialize every field that is instance of Serializble
        map.put(SERIALIZED_OBJECT_KEY, getName());

        return map;
    }

    @Override
    public @NotNull ItemStack deserialize(@NotNull Object value) throws IllegalArgumentException {
        if (value instanceof ItemStack itemStack) return itemStack;

        if (value instanceof Map<?, ?> rawMap) {
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                map.put((String)entry.getKey(), entry.getValue());
            }

            return ItemStack.deserialize(map);
        }

        return new ItemStack(Material.AIR);
    }

    @Override
    public boolean canHandle(@NotNull Object value) {
        return (value instanceof ItemStack || value instanceof Map<?,?>);
    }

    @Override
    public @Nullable ItemStack parse(@Nullable Object value, @Nullable ItemStack defaultValue) {
        if (value instanceof ItemStack itemStack) return itemStack;

        if (value instanceof Map<?, ?> rawMap) {
            Map<String, Object> map = new HashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                map.put((String)entry.getKey(), entry.getValue());
            }

            return ItemStack.deserialize(map);
        }

        return defaultValue;
    }
}
