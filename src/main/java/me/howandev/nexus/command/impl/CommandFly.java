package me.howandev.nexus.command.impl;

import me.howandev.nexus.command.Argument;
import me.howandev.nexus.command.CommandUtil;
import me.howandev.nexus.command.SimpleCommand;
import me.howandev.nexus.command.sender.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.howandev.nexus.locale.Message.*;

public class CommandFly extends SimpleCommand {
    public CommandFly() {
        super("fly", "command.fly.description", "command.fly");
    }

    @Override
    public Optional<List<Argument>> getArguments() {
        List<Argument> args = new ArrayList<>();
        args.add(new Argument("player", "player", false));
        return Optional.of(args);
    }

    @Override
    public void execute(JavaPlugin plugin, Sender sender, List<String> args, String label) throws Exception {
        String permission = getPermission();
        if (permission != null && !sender.hasPermission(permission)) {
            COMMAND_MISSING_PERMISSION.send(sender, label);
            return;
        }

        switch (args.size()) {
            // Setting own game mode
            case 0 -> {
                if (!sender.hasPermission("command.fly.self") || sender.isConsole()) {
                    COMMAND_MISSING_PERMISSION.send(sender, label);
                    return;
                }

                Player player = Bukkit.getPlayer(sender.getUniqueId());
                if (player == null || !player.isOnline()) {
                    return;
                }

                boolean state = player.isFlying();
                player.setFallDistance(0f);
                if (state) {
                    player.setAllowFlight(false);
                    player.setFlying(false);
                    COMMAND_FLIGHT_DISABLED_SELF.send(sender);
                    plugin.getLogger().info(player.getName() + "'s flight ability disabled");
                    return;
                }

                player.setAllowFlight(true);
                COMMAND_FLIGHT_ENABLED_SELF.send(sender);
                plugin.getLogger().info(player.getName() + "'s flight ability enabled");
            }

            // Setting someone's flight
            case 1 -> {
                if (!sender.hasPermission("command.fly.other")) {
                    COMMAND_MISSING_PERMISSION.send(sender, label);
                    return;
                }

                String targetArgument = args.get(0);
                Player target = CommandUtil.getVisiblePlayer(sender, targetArgument);
                if (target == null || !target.isOnline()) {
                    SEARCH_PLAYER_NOT_FOUND.send(sender);
                    return;
                }

                boolean state = target.isFlying();
                target.setFallDistance(0f);
                if (state) {
                    target.setAllowFlight(false);
                    target.setFlying(false);
                    COMMAND_FLIGHT_DISABLED_OTHER.send(sender, target.getName());
                    plugin.getLogger().info((sender.isConsole() ? "(CONSOLE)" : sender.getName()) + " disabled " + target.getName() + "'s flight ability");
                    return;
                }

                target.setAllowFlight(true);
                COMMAND_FLIGHT_ENABLED_OTHER.send(sender, target.getName());
                plugin.getLogger().info((sender.isConsole() ? "(CONSOLE)" : sender.getName()) + " enabled " + target.getName() + "'s flight ability");
            }

            default -> throw new IllegalStateException();
        }
    }
}
