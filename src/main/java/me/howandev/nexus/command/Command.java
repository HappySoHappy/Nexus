package me.howandev.nexus.command;

import lombok.Getter;
import me.howandev.nexus.command.sender.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

import static me.howandev.nexus.locale.Message.*;

@Getter
public abstract class Command<T> {
    private final @NotNull String name;
    private final @NotNull List<String> aliases;
    private final @NotNull Component description;
    private final @NotNull Predicate<Integer> argumentCheck;
    private final @Nullable String permission;
    public Command(@NotNull String name, String descriptionKey, @Nullable String permission) {
        this.name = name;
        this.aliases = new ArrayList<>();
        this.description = COMMAND_DESCRIPTION.build(descriptionKey);
        this.permission = permission;
        this.argumentCheck = integer -> {
            // There are no arguments accepted
            if (getArguments().isEmpty()) return false;

            List<Argument> arguments = getArguments().get();
            if (integer >= arguments.stream().filter(Argument::isRequired).toList().size()
                    && integer <= arguments.stream().toList().size()) return true;

            // Check if there are enough required arguments
            return integer == arguments.stream().filter(Argument::isRequired).toList().size();
        };
    }

    public Command(@NotNull String name, @NotNull List<String> aliases, String descriptionKey, @Nullable String permission) {
        this.name = name;
        this.aliases = aliases;
        this.description = COMMAND_DESCRIPTION.build(descriptionKey);
        this.permission = permission;
        this.argumentCheck = integer -> {
            // There are no arguments accepted
            if (getArguments().isEmpty()) return false;

            List<Argument> arguments = getArguments().get();
            if (integer >= arguments.stream().filter(Argument::isRequired).toList().size()
                    && integer <= arguments.stream().toList().size()) return true;

            // Check if there are enough required arguments
            return integer == arguments.stream().filter(Argument::isRequired).toList().size();
        };
    }

    // Specification method - default implementation is provided as some commands do not require additional specification.
    public Optional<List<Specification>> getSpecification() {
        return Optional.empty();
    }

    // Convenience methods for specification
    public boolean specifiesSyncExecution() {
        return getSpecification().isPresent()
                && getSpecification().get().contains(Specification.SYNCHRONOUS_EXECUTION);
    }

    public boolean specifiesConsoleOnly() {
        return getSpecification().isPresent()
                && getSpecification().get().contains(Specification.CONSOLE_ONLY);
    }

    public abstract Optional<List<Argument>> getArguments();

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

    // Main execution method for the command.
    public abstract void execute(JavaPlugin plugin, Sender sender, T target, List<String> args, String label) throws Exception;

    // Tab completion method - default implementation is provided as some commands do not provide tab completions.
    public List<String> tabComplete(JavaPlugin plugin, Sender sender, List<String> args) {
        return Collections.emptyList();
    }

    /**
     * Returns true if the sender is authorised to use this command
     * Commands with children are likely to override this method to check for permissions based upon whether
     * a sender has access to any sub commands.
     *
     * @param sender the sender
     * @return true if the sender has permission to use this command
     */
    public boolean isAuthorized(Sender sender) {
        return permission == null || sender.hasPermission(permission);
    }

    /**
     * Gets if this command should be displayed in command listings, or "hidden"
     *
     * @return if the command should be displayed
     */
    public boolean shouldDisplay() {
        return true;
    }

    protected final void replaceArgs(List<String> args, int index, Function<String, String> rewrites) {
        String arg = args.get(index).toLowerCase(Locale.ROOT);
        String rewrite = rewrites.apply(arg);
        if (rewrite != null) {
            args.remove(index);
            args.add(index, rewrite);
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
