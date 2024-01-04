package me.howandev.nexus.command;

import me.howandev.nexus.command.sender.Sender;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public abstract class ChildCommand<T> extends Command<T> {
    public ChildCommand(@NotNull String name, String descriptionKey, @Nullable String permission) {
        super(name, descriptionKey, permission);
    }

    public ChildCommand(@NotNull String name, String descriptionKey, @Nullable String permission, Predicate<Integer> argumentCheck) {
        super(name, descriptionKey, permission, argumentCheck);
    }

    public ChildCommand(@NotNull String name, @NotNull List<String> aliases, String descriptionKey, @Nullable String permission) {
        super(name, aliases, descriptionKey, permission);
    }

    public ChildCommand(@NotNull String name, @NotNull List<String> aliases, String descriptionKey, @Nullable String permission, Predicate<Integer> argumentCheck) {
        super(name, aliases, descriptionKey, permission, argumentCheck);
    }

    public abstract Optional<List<Argument>> getArguments();

    @Override
    public void sendUsage(Sender sender, String label) {
        sender.sendMessage("usagesub");
    }

    @Override
    public void sendDetailedUsage(Sender sender, String label) {
        sender.sendMessage("detailed usagesub");
    }

    public abstract void execute(JavaPlugin plugin, Sender sender, T target, List<String> args, String label) throws Exception;
}
