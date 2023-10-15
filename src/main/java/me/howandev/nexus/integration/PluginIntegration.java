package me.howandev.nexus.integration;

import lombok.Getter;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Logger;

public abstract class PluginIntegration {
    @Getter
    private final Plugin plugin;
    @Getter
    private final Logger logger;
    public PluginIntegration(final @NotNull Plugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }
    public abstract boolean isAvailable();
    public abstract void initialize();
}
