package me.howandev.nexus.integration.luckperms;

import me.howandev.nexus.integration.PluginIntegration;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.query.QueryOptions;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;

public class LuckPermsIntegration extends PluginIntegration {
    private LuckPerms api;
    public LuckPermsIntegration(@NotNull Plugin plugin) {
        super(plugin);
    }

    @Override
    public boolean isAvailable() {
        return Bukkit.getPluginManager().getPlugin("LuckPerms") != null;
    }

    @Override
    public void initialize() {
        if (!isAvailable()) throw new IllegalStateException("Unable to initialize LuckPerms integration - Unavailable");

        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            api = provider.getProvider();
            getLogger().info("Successfully initialized LuckPerms integration!");
            return;
        }

        throw new IllegalStateException("Unable to initialize LuckPerms integration - Provider is missing");
    }

    /**
     * Returns internal name of primary group of player
     *
     * @param player Player
     * @return internal name of primary group of player
     */
    public @Nullable String getPrimaryGroup(Player player) {
        if (api == null) return null;

        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return null;

        return user.getPrimaryGroup();
    }

    /**
     * Checks whether player has permission. Operator players have all permissions.
     *
     * @param player Player
     * @param permission Permission node to check
     * @return true if player explicitly has permission, and node's value is true
     */
    public boolean hasPermission(Player player, String permission) {
        if (api == null) return false;

        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return false;

        for (Node node : user.getNodes()) {
            if (node.getKey().equalsIgnoreCase(permission)) {
                return node.getValue();
            }
        }

        return false;
    }

    /**
     * Checks whether player has permission, ignores operator status.
     *
     * @param player Player
     * @param permission Permission node to check
     * @return true if player explicitly has permission node set, and node's value is true
     */
    public boolean hasExplicitPermission(Player player, String permission) {
        if (api == null) return false;

        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return false;

        for (Node node : user.resolveInheritedNodes(QueryOptions.defaultContextualOptions())) {
            if (node.getKey().equalsIgnoreCase(permission)) {
                return node.getValue();
            }
        }

        return false;
    }

    public @Nullable Duration getPermissionExpiry(Player player, String permission) {
        if (api == null) return null;

        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return null;

        for (Node node : user.getNodes()) {
            if (node.getKey().equalsIgnoreCase(permission)) {
                if (!node.hasExpiry()) return null;

                return node.getExpiryDuration();
            }
        }

        return null;
    }

}
