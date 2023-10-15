package me.howandev.nexus.command.sender;

import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Wrapper interface to represent a CommandSender/CommandSource within the command implementations.
 */
public interface Sender {
    /** The uuid used by the console sender. */
    UUID CONSOLE_UUID = new UUID(0, 0); // 00000000-0000-0000-0000-000000000000

    /** The name used by the console sender. */
    String CONSOLE_NAME = "Console";

    /**
     * Gets the plugin instance the sender is from.
     *
     * @return the plugin
     */
    @NotNull JavaPlugin getPlugin();

    /**
     * Gets the sender's unique id.
     *
     * <p>See {@link #CONSOLE_UUID} for the console's UUID representation.</p>
     *
     * @return the sender's uuid
     */
    @NotNull UUID getUniqueId();

    /**
     * Gets the sender's name.
     *
     * @return a friendly name for the sender
     */
    @NotNull String getName();

    /**
     * Gets whether this sender is the console.
     *
     * @return if the sender is the console
     */
    boolean isConsole();
    /**
     * Send a json message to the Sender.
     *
     * @param message the message to send.
     */
    void sendMessage(Component message);

    /**
     * Send a message to the Sender.
     *
     * @param message the message to send.
     */
    void sendMessage(String message);

    /**
     * Makes the sender perform a command.
     *
     * @param commandLine the command
     */
    void performCommand(String commandLine);

    /**
     * Check if the Sender has a permission.
     *
     * @param permission the permission to check for
     * @return true if the sender has the permission
     */
    boolean hasPermission(String permission);
}
