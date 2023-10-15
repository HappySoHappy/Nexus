package me.howandev.nexus.command.sender;

import me.howandev.nexus.command.sender.Sender;
import me.howandev.nexus.command.sender.SenderFactory;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class BukkitSenderFactory extends SenderFactory<JavaPlugin, CommandSender> {
    private final BukkitAudiences audiences;
    public BukkitSenderFactory(JavaPlugin plugin) {
        super(plugin);
        this.audiences = BukkitAudiences.create(plugin);
    }

    @Override
    protected String getName(CommandSender sender) {
        if (sender instanceof Player) {
            return sender.getName();
        }
        return Sender.CONSOLE_NAME;
    }

    @Override
    protected UUID getUniqueId(CommandSender sender) {
        if (sender instanceof Player) {
            return ((Player) sender).getUniqueId();
        }

        return Sender.CONSOLE_UUID;
    }

    @Override
    protected void sendMessage(CommandSender sender, Component message) {
        // we can safely send async for players and the console - otherwise, send it sync
        if (sender instanceof Player || sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender) {
            this.audiences.sender(sender).sendMessage(message);
        } else {
            Bukkit.getScheduler().runTask(getPlugin(), bukkitTask -> this.audiences.sender(sender).sendMessage(message));
            //getPlugin().getBootstrap().getScheduler().executeSync(() -> this.audiences.sender(sender).sendMessage(message));
        }
    }

    @Override
    protected void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(message);
    }

    @Override
    protected boolean hasPermission(CommandSender sender, String node) {
        return sender.hasPermission(node);
    }

    @Override
    protected void performCommand(CommandSender sender, String command) {
        getPlugin().getServer().dispatchCommand(sender, command);
    }

    @Override
    protected boolean isConsole(CommandSender sender) {
        return sender instanceof ConsoleCommandSender || sender instanceof RemoteConsoleCommandSender;
    }

    @Override
    public void close() {
        this.audiences.close();
    }
}
