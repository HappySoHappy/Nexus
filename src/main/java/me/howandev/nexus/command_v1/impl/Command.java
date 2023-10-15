package me.howandev.nexus.command_v1.impl;

import me.howandev.nexus.command.Argument;
import me.howandev.nexus.command.sender.Sender;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * An abstract command_v1 class
 *
 * @param <T> the argument type required by the command_v1
 */
public abstract class Command<T> {
    /**
     * The name of the command_v1. Should be properly capitalised.
     */
    private final @NotNull String name;

    /**
     * The permission required to use this command_v1. Nullable.
     */
    private final @Nullable String permission;

    /**
     * A predicate used for testing the size of the arguments list passed to this command_v1
     */
    private final @NotNull Predicate<Integer> argumentCheck;

    public Command(@NotNull String name, @Nullable String permission, @NotNull Predicate<Integer> argumentCheck) {
        this.name = name;
        this.permission = permission;
        this.argumentCheck = argumentCheck;
    }

    /**
     * Gets the short name of this command_v1
     *
     * <p>The result should be appropriately capitalised.</p>
     *
     * @return the command_v1 name
     */
    public @NotNull String getName() {
        return this.name;
    }

    /**
     * Gets the permission required by this command_v1, if present
     *
     * @return the command_v1 permission
     */
    public @NotNull Optional<String> getPermission() {
        return Optional.ofNullable(this.permission);
    }

    /**
     * Gets the predicate used to validate the number of arguments provided to
     * the command_v1 on execution
     *
     * @return the argument checking predicate
     */
    public @NotNull Predicate<Integer> getArgumentCheck() {
        return this.argumentCheck;
    }

    /**
     * Gets the commands description.
     *
     * @return the description
     */
    public abstract Component getDescription();

    /**
     * Gets the usage of this command_v1.
     * Will only return a non-empty result for main commands.
     *
     * @return the usage of this command_v1.
     */
    public abstract String getUsage();

    /**
     * Gets the arguments required by this command_v1
     *
     * @return the commands arguments
     */
    public abstract Optional<List<Argument>> getArgs();

    // Main execution method for the command_v1.
    public abstract void execute(JavaPlugin plugin, Sender sender, T target, List<String> args, String label) throws Exception;

    // Tab completion method - default implementation is provided as some commands do not provide tab completions.
    public List<String> tabComplete(JavaPlugin plugin, Sender sender, List<String> args) {
        return Collections.emptyList();
    }

    /**
     * Sends a brief command usage message to the Sender.
     * If this command has child commands, the children are listed. Otherwise, a basic usage message is sent.
     *
     * @param sender the sender to send the usage to
     * @param label the label used when executing the command
     */
    public abstract void sendUsage(Sender sender, String label);

    /**
     * Sends a detailed command usage message to the Sender.
     * If this command has child commands, nothing is sent. Otherwise, a detailed messaging containing a description
     * and argument usage is sent.
     *
     * @param sender the sender to send the usage to
     * @param label the label used when executing the command
     */
    public abstract void sendDetailedUsage(Sender sender, String label);

    /**
     * Returns true if the sender is authorised to use this command
     * Commands with children are likely to override this method to check for permissions based upon whether
     * a sender has access to any sub commands.
     *
     * @param sender the sender
     * @return true if the sender has permission to use this command
     */
    public boolean isAuthorized(Sender sender) {
        return this.permission == null;
    }

    /**
     * Gets if this command should be displayed in command listings, or be "hidden"
     *
     * @return if the command should be displayed
     */
    public boolean shouldDisplay() {
        return true;
    }
}
