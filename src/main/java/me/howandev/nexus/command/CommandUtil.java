package me.howandev.nexus.command;

import me.howandev.nexus.command.sender.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandUtil {
    private CommandUtil() { }

    public static @NotNull List<Player> getOnlinePlayers() {
        return new ArrayList<>(Bukkit.getOnlinePlayers());
    }

    public static @NotNull List<Player> getVisiblePlayers(Sender viewer) {
        return new ArrayList<>(
                Bukkit.getOnlinePlayers() //TODO: vanished
        );
    }

    public static Player getOnlinePlayer(String playerName) {
        return Bukkit.getPlayer(playerName);
    }

    public static Player getOnlinePlayer(UUID uniqueId) {
        return Bukkit.getPlayer(uniqueId);
    }

    public static Player getVisiblePlayer(Sender viewer, String playerName) {
        return Bukkit.getPlayer(playerName); //TODO: vanished
    }

    public static Player getVisiblePlayer(Sender viewer, UUID uniqueId) {
        return Bukkit.getPlayer(uniqueId); //TODO: vanished
    }
}
