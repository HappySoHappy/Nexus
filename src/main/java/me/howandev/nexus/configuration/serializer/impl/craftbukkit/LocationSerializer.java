package me.howandev.nexus.configuration.serializer.impl.craftbukkit;

import me.howandev.nexus.configuration.Serializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class LocationSerializer implements Serializer<Location> {
    @Override
    public @NotNull String getName() {
        return "Location";
    }

    @Override
    public @NotNull Map<String, Object> serialize(@NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof Location location)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        Map<String, Object> map = new LinkedHashMap<>();
        //todo: this needs more work and should be automatically done
        // doesnt matter for me, but would be cool
        map.put(SERIALIZED_OBJECT_KEY, getName());
        map.put("x", location.getX());
        map.put("y", location.getX());
        map.put("z", location.getX());
        map.put("yaw", location.getYaw());
        map.put("pitch", location.getPitch());
        if (location.getWorld() != null)
            map.put("world", location.getWorld().getName());

        return map;
    }

    @Override
    public @NotNull Location deserialize(@NotNull Object value) throws IllegalArgumentException {
        if (value instanceof Location location) {
            return location;
        }

        if (value instanceof Map<?, ?> map) {
            double x, y, z, yaw, pitch;
            x = (double) map.get("x");
            y = (double) map.get("y");
            z = (double) map.get("z");

            yaw = (double) map.get("yaw");
            pitch = (double) map.get("pitch");
            String worldName = (String) map.get("world");

            World world = Bukkit.getWorld(worldName);

            return new Location(world, x, y, z, ((float) yaw), ((float) pitch));
        }

        throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");
    }

    @Override
    public boolean canHandle(@NotNull Object value) {
        return false;
    }

    @Override
    public @Nullable Location parse(@Nullable Object value, @Nullable Location defaultValue) {
        if (value instanceof Location location) {
            return location;
        }

        if (value instanceof Map<?, ?> map) {
            double x, y, z, yaw, pitch;
            x = (double) map.get("x");
            y = (double) map.get("y");
            z = (double) map.get("z");
            yaw = (double) map.get("yaw");
            pitch = (double) map.get("pitch");

            World world = null;
            if (map.containsKey("world")) {
                String worldName = (String) map.get("world");
                world = Bukkit.getWorld(worldName);
            }

            return new Location(world, x, y, z, ((float) yaw), ((float) pitch));
        }

        return defaultValue;
    }
}
