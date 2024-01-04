package me.howandev.nexus.command;

import me.howandev.nexus.command.sender.Sender;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static me.howandev.nexus.locale.Message.COMMAND_WRONG_USAGE;

/**
 * Represents a single top level command
 */
public abstract class SimpleCommand extends Command<Void> {
    public SimpleCommand(@NotNull String name, @NotNull String descriptionKey, @Nullable String permission) {
        super(name, descriptionKey, permission);
    }

    public SimpleCommand(@NotNull String name, @NotNull List<String> aliases, @NotNull String descriptionKey, @Nullable String permission) {
        super(name, aliases, descriptionKey, permission);
    }

    @Override
    public void sendUsage(Sender sender, String label) {
        Component properUsage = Component.text("/"+label);

        if (getArguments().isPresent()) {
            Component properUsageArguments = properUsage;
            for (Argument argument : getArguments().get()) {
                properUsageArguments = properUsageArguments.appendSpace();
                properUsageArguments = properUsageArguments.append(argument.asPrettyString());
            }

            COMMAND_WRONG_USAGE.send(sender, properUsageArguments);
            return;
        }

        COMMAND_WRONG_USAGE.send(sender, properUsage);
    }

    @Override
    public void sendDetailedUsage(Sender sender, String label) {
        throw new IllegalStateException("not implemented");
    }

    @Override
    public void execute(JavaPlugin plugin, Sender sender, Void ignored, List<String> args, String label) throws Exception {
        execute(plugin, sender, args, label);
    }

    public abstract void execute(JavaPlugin plugin, Sender sender, List<String> args, String label) throws Exception;
}
