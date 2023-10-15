package me.howandev.nexus.command.sender;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public final class GenericSender<T> implements Sender {
    private final JavaPlugin plugin;
    private final SenderFactory<?, T> factory;
    private final T sender;
    GenericSender(JavaPlugin plugin, SenderFactory<?, T> factory, T sender) {
        this.plugin = plugin;
        this.factory = factory;
        this.sender = sender;
    }

    @Override
    public @NotNull JavaPlugin getPlugin() {
        return plugin;
    }

    @Override
    public @NotNull UUID getUniqueId() {
        return factory.getUniqueId(sender);
    }

    @Override
    public @NotNull String getName() {
        return factory.getName(sender);
    }

    @Override
    public boolean isConsole() {
        return factory.isConsole(sender);
    }

    @Override
    public void sendMessage(Component message) {
        if (isConsole()) {
            for (Component line : splitNewlines(message)) {
                factory.sendMessage(sender, line);
            }

            return;
        }

        factory.sendMessage(sender, message);
    }

    @Override
    public void sendMessage(String message) {
        factory.sendMessage(sender, message);
    }

    @Override
    public void performCommand(String commandLine) {
        factory.performCommand(sender, commandLine);
    }

    @Override
    public boolean hasPermission(String permission) {
        return (isConsole() && factory.consoleHasAllPermissions()) || factory.hasPermission(sender, permission);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof final GenericSender<?> that)) return false;

        return this.getUniqueId().equals(that.getUniqueId());
    }

    @Override
    public int hashCode() {
        return getUniqueId().hashCode();
    }

    @Override
    public String toString() {
        return getName();
    }

    private static Iterable<Component> splitNewlines(Component message) {
        if (message instanceof TextComponent textComponent
                && textComponent.content().isEmpty()
                && message.style().isEmpty()
                && !message.children().isEmpty()) {
            LinkedList<List<Component>> split = new LinkedList<>();
            split.add(new ArrayList<>());

            for (Component child : message.children()) {
                if (Component.newline().equals(child)) {
                    split.add(new ArrayList<>());
                    continue;
                }

                Iterator<Component> splitChildren = splitNewlines(child).iterator();
                if (splitChildren.hasNext())
                    split.getLast().add(splitChildren.next());

                while (splitChildren.hasNext()) {
                    split.add(new ArrayList<>());
                    split.getLast().add(splitChildren.next());
                }
            }

            return split.stream().map(input -> switch (input.size()) {
                case 0 -> Component.empty();
                case 1 -> input.get(0);
                default -> Component.join(JoinConfiguration.separator(Component.empty()), input);
            }).collect(Collectors.toList());
        }

        return Collections.singleton(message);
    }
}
