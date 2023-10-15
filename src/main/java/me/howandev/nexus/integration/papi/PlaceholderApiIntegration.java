package me.howandev.nexus.integration.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import me.howandev.nexus.integration.PluginIntegration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class PlaceholderApiIntegration extends PluginIntegration {
    public PlaceholderApiIntegration(@NotNull Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }

    @Override
    public void initialize() {
        if (!isAvailable()) throw new IllegalStateException("Unable to initialize PlaceholderAPI integration - Unavailable");

        getLogger().info("Successfully initialized PlaceholderAPI integration!");
    }

    public @NotNull String setPlaceholders(final @NotNull Player player, final @NotNull String text) {
        if (!isAvailable()) return text;

        return PlaceholderAPI.setPlaceholders(player, text);
    }
}
