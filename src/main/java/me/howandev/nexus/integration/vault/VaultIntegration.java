package me.howandev.nexus.integration.vault;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.howandev.nexus.integration.PluginIntegration;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

//todo: remove vault
public class VaultIntegration extends PluginIntegration {
    @Getter
    @Accessors(fluent = true)
    private @Nullable Economy economy;

    @Getter
    @Accessors(fluent = true)
    private @Nullable Chat chat;

    @Getter
    @Accessors(fluent = true)
    private @Nullable Permission permission;

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

        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
            getLogger().info("Successfully enabled Economy provider from '"+economyProvider.getPlugin()+"'!");
        }

        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if (chatProvider != null) {
            chat = chatProvider.getProvider();
            getLogger().info("Successfully enabled Chat provider from '"+chatProvider.getPlugin()+"'!");
        }

        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
            getLogger().info("Successfully enabled Permission provider from '"+permissionProvider.getPlugin()+"'!");
        }

        getLogger().info("Successfully initialized Vault integration!");
    }

    public @Nullable String getPrimaryGroup(final @NotNull Player player) {
        if (permission == null || !permission.hasGroupSupport()) return null;

        return permission.getPrimaryGroup(player);
    }
}
