package me.howandev.nexus.integration.vault;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.howandev.nexus.NexusPlugin;
import me.howandev.nexus.integration.PluginIntegration;
import me.howandev.nexus.integration.vault.provider.NexusEconomy;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VaultIntegration extends PluginIntegration {
    @Getter
    @Accessors(fluent = true)
    private @Nullable Economy economy;

    public VaultIntegration(final @NotNull Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("Vault") != null;
    }

    @Override
    public void initialize() {
        if (!isAvailable()) throw new IllegalStateException("Unable to initialize Vault integration - Unavailable");


        Bukkit.getServer().getServicesManager().register(Economy.class, new NexusEconomy(), NexusPlugin.getInstance(), ServicePriority.Highest);

        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            getLogger().info("Successfully enabled Economy provider from '"+economyProvider.getPlugin()+"'!");
        }

        getLogger().info("Successfully initialized Vault integration!");
    }
}
