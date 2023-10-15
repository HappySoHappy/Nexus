package me.howandev.nexus.command.sender;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public abstract class SenderFactory<P extends JavaPlugin, S> implements AutoCloseable {
    @Getter
    private final P plugin;
    public SenderFactory(P plugin) {
        this.plugin = plugin;
    }

    public final Sender wrap(S sender) {
        return new GenericSender<>(plugin, this, sender);
    }

    protected abstract UUID getUniqueId(S sender);

    protected abstract String getName(S sender);

    protected abstract boolean isConsole(S sender);

    protected abstract void sendMessage(S sender, Component message);

    protected abstract void sendMessage(S sender, String message);

    protected abstract void performCommand(S sender, String command);

    protected boolean consoleHasAllPermissions() {
        return true;
    }

    protected abstract boolean hasPermission(S sender, String node);
}
