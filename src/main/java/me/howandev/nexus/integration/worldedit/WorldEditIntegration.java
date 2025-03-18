package me.howandev.nexus.integration.worldedit;

import me.howandev.nexus.integration.PluginIntegration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class WorldEditIntegration extends PluginIntegration {


    public WorldEditIntegration(@NotNull Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void initialize() {

    }


}
