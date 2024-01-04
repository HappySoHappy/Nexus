package me.howandev.nexus.command.impl;

import me.howandev.nexus.command.Argument;
import me.howandev.nexus.command.CommandUtil;
import me.howandev.nexus.command.SimpleCommand;
import me.howandev.nexus.command.sender.Sender;
import me.howandev.nexus.command.tab.CompletionSupplier;
import me.howandev.nexus.command.tab.TabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.howandev.nexus.locale.Message.*;

public class CommandSpeed extends SimpleCommand {
    public static final float FLY_SPEED = 0.1f;
    public static final float WALK_SPEED = 0.2f;
    public CommandSpeed() {
        super("speed", "command.speed.description", "command.speed");
    }

    @Override
    public Optional<List<Argument>> getArguments() {
        List<Argument> args = new ArrayList<>();
        args.add(new Argument("number", "number"));
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
            case 1 -> {
                if (!sender.hasPermission("command.speed.self") || sender.isConsole()) {
                    COMMAND_MISSING_PERMISSION.send(sender, label);
                    return;
                }

                Player player = Bukkit.getPlayer(sender.getUniqueId());
                if (player == null || !player.isOnline()) {
                    return;
                }

                boolean flying = player.isFlying();

                float speed = parseFloat(args.get(0), flying ? (FLY_SPEED * 10) : (WALK_SPEED * 10));
                speed = Math.min(Math.max(speed / 10, -1), 1);
                if (flying) {
                    player.setFlySpeed(speed);
                    COMMAND_SPEED_SET_FLY_SELF.send(sender, speed);
                    plugin.getLogger().info(player.getName() + "'s flying speed changed to "+speed);
                    return;
                }

                player.setWalkSpeed(speed);
                COMMAND_SPEED_SET_WALK_SELF.send(sender, speed);
                plugin.getLogger().info(player.getName() + "'s walking speed changed to "+speed);
            }

            // Setting someone's speed
            case 2 -> {
                if (!sender.hasPermission("command.fly.other")) {
                    COMMAND_MISSING_PERMISSION.send(sender, label);
                    return;
                }

                String targetArgument = args.get(1);
                Player target = CommandUtil.getVisiblePlayer(sender, targetArgument);
                if (target == null || !target.isOnline()) {
                    SEARCH_PLAYER_NOT_FOUND.send(sender);
                    return;
                }

                boolean flying = target.isFlying();

                float speed = parseFloat(args.get(0), flying ? (FLY_SPEED * 10) : (WALK_SPEED * 10));
                speed = Math.min(Math.max(speed / 10, -1), 1);
                if (flying) {
                    target.setFlySpeed(speed);
                    COMMAND_SPEED_SET_FLY_OTHER.send(sender, target.getName(), speed);
                    plugin.getLogger().info((sender.isConsole() ? "(CONSOLE)" : sender.getName()) + " changed " + target.getName() + "'s flying speed to "+speed);
                    return;
                }

                target.setWalkSpeed(speed);
                COMMAND_SPEED_SET_WALK_OTHER.send(sender, target.getName(), speed);
                plugin.getLogger().info((sender.isConsole() ? "(CONSOLE)" : sender.getName()) + " changed " + target.getName() + "'s walking speed to "+speed);
            }

            default -> throw new IllegalStateException();
        }
    }

    @Override
    public List<String> tabComplete(JavaPlugin plugin, Sender sender, List<String> args) {
        return TabCompleter.create()
                .at(0, CompletionSupplier.startsWith("default", "1", "1.5", "1.75", "2")
                )

                /*.at(1, CompletionSupplier.startsWith(
                                //TODO: add check for vanished...
                                () -> sender.hasPermission("command.feed.other")
                                        ? CommandUtil.getVisiblePlayers(sender).stream().map(Player::getName)
                                        : Stream.empty()
                        )
                )*/
                .complete(args);
    }

    private float parseFloat(String value, float def) {
        try {
            return Float.parseFloat(value);
        } catch (Exception ex) {
            return def;
        }
    }
}
