package me.howandev.nexus.integration;

import lombok.Getter;
import lombok.experimental.Accessors;
import me.howandev.nexus.integration.papi.PlaceholderApiIntegration;
import me.howandev.nexus.integration.vault.VaultIntegration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class IntegrationManager {
    @Getter
    @Accessors(fluent = true)
    private final Plugin plugin;

    @Getter
    @Accessors(fluent = true)
    private final @NotNull VaultIntegration vaultIntegration;

    @Getter
    @Accessors(fluent = true)
    private final @NotNull PlaceholderApiIntegration papiIntegration;
    public IntegrationManager(final @NotNull Plugin plugin) {
        this.plugin = plugin;

        vaultIntegration = new VaultIntegration(plugin);
        papiIntegration = new PlaceholderApiIntegration(plugin);
    }

    public void initializeIntegrations() {
        if (vaultIntegration.isAvailable())
            vaultIntegration.initialize();

        if (papiIntegration.isAvailable())
            papiIntegration.initialize();
    }
}
