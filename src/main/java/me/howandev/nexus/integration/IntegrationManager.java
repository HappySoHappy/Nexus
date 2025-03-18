package me.howandev.nexus.integration;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.howandev.nexus.integration.luckperms.LuckPermsIntegration;
import me.howandev.nexus.integration.papi.PlaceholderApiIntegration;
import me.howandev.nexus.integration.vault.VaultIntegration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class IntegrationManager {
    @Getter
    @Accessors(fluent = true)
    private final Plugin plugin;

    @Getter
    private final @NotNull LuckPermsIntegration luckPerms;

    @Getter
    private final @NotNull PlaceholderApiIntegration placeholderApi;

    @Getter
    @Accessors(fluent = true)
    @Deprecated
    private final @NotNull VaultIntegration vaultIntegration;
    public IntegrationManager(final @NotNull Plugin plugin) {
        this.plugin = plugin;

        luckPerms = new LuckPermsIntegration(plugin);

        vaultIntegration = new VaultIntegration(plugin);
        placeholderApi = new PlaceholderApiIntegration(plugin);
    }

    public void initializeIntegrations() {
        if (luckPerms.isAvailable())
            luckPerms.initialize();

        if (placeholderApi.isAvailable())
            placeholderApi.initialize();

        if (vaultIntegration.isAvailable())
            vaultIntegration.initialize();
    }
}
