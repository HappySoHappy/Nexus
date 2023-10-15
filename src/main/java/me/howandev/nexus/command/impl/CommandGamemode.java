package me.howandev.nexus.command.impl;

import me.howandev.nexus.command.Argument;
import me.howandev.nexus.command.CommandUtil;
import me.howandev.nexus.command.SingleCommand;
import me.howandev.nexus.command.Specification;
import me.howandev.nexus.command.sender.Sender;
import me.howandev.nexus.command.tab.CompletionSupplier;
import me.howandev.nexus.command.tab.TabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.stream.Stream;

import static me.howandev.nexus.locale.Message.*;

public class CommandGamemode extends SingleCommand {
    public static final Map<String, String> GAMEMODE_PERMISSIONS = Map.ofEntries(
            Map.entry("survival", "command.gamemode.survival"),
            Map.entry("creative", "command.gamemode.creative"),
            Map.entry("adventure", "command.gamemode.adventure"),
            Map.entry("spectator", "command.gamemode.spectator")
    );
    public CommandGamemode() {
        super("gamemode", List.of("gm"), "command.gamemode.description", "command.gamemode");
    }

    @Override
    public Optional<List<Specification>> getSpecification() {
        return Optional.of(List.of(Specification.SYNCHRONOUS_EXECUTION));
    }

    @Override
    public Optional<List<Argument>> getArguments() {
        List<Argument> args = new ArrayList<>();
        args.add(new Argument("gamemode", "gamemode"));
        args.add(new Argument("player", "player", false));
        return Optional.of(args);
    }

    @Override
    public void execute(JavaPlugin plugin, Sender sender, List<String> args, String label) {
        applyConvenienceAliases(args, true);

        String gamemodeArgument = args.get(0).toLowerCase(Locale.ROOT);
        GameMode gameMode = switch (gamemodeArgument) {
            case "survival" -> GameMode.SURVIVAL;
            case "creative" -> GameMode.CREATIVE;
            case "adventure" -> GameMode.ADVENTURE;
            case "spectator" -> GameMode.SPECTATOR;
            default -> null;
        };

        if (gameMode == null) {
            COMMAND_GAMEMODE_UNKNOWN_MODE.send(sender, gamemodeArgument);
            return;
        }

        // This should never be null since we've already passed switch statement
        String permission = GAMEMODE_PERMISSIONS.get(gamemodeArgument);
        if (permission != null && !sender.hasPermission(permission)) {
            COMMAND_GAMEMODE_MISSING_MODE_PERMISSION.send(sender, gamemodeArgument);
            return;
        }

        switch (args.size()) {
            // Setting own game mode
            case 1 -> {
                if (!sender.hasPermission("command.gamemode.self") || sender.isConsole()) {
                    return;
                }

                Player player = Bukkit.getPlayer(sender.getUniqueId());
                if (player == null || !player.isOnline()) {
                    return;
                }

                // Ignore if we are changing to the same mode
                if (player.getGameMode() == gameMode) {
                    return;
                }

                player.setGameMode(gameMode);
                COMMAND_GAMEMODE_SET_SELF.send(sender, gamemodeArgument);
                plugin.getLogger().info(player.getName() + "'s gamemode set to "+gamemodeArgument);
            }

            // Setting someone's game mode
            case 2 -> {
                if (!sender.hasPermission("command.gamemode.other")) {
                    return;
                }

                String targetArgument = args.get(1);
                Player target = CommandUtil.getVisiblePlayer(sender, targetArgument);
                if (target == null || !target.isOnline()) {
                    SEARCH_PLAYER_NOT_FOUND.send(sender);
                    return;
                }

                if (target.getUniqueId() == sender.getUniqueId()) {
                    return;
                }

                // Ignore if we are changing to the same mode
                if (target.getGameMode() == gameMode) {
                    return;
                }

                target.setGameMode(gameMode);
                COMMAND_GAMEMODE_SET_OTHER.send(sender, target.getName(), gamemodeArgument);
                plugin.getLogger().info((sender.isConsole() ? "(CONSOLE)" : sender.getName()) + " changed " + target.getName() + "'s gamemode to "+gamemodeArgument);
            }

            default -> throw new IllegalStateException();
        }
    }

    @Override
    public List<String> tabComplete(JavaPlugin plugin, Sender sender, List<String> args) {
        applyConvenienceAliases(args, false);

        return TabCompleter.create()
                .at(0, CompletionSupplier.startsWith(
                        GAMEMODE_PERMISSIONS.entrySet().stream()
                                .filter(entry -> sender.hasPermission(entry.getValue()))
                                .map(Map.Entry::getKey).toList()
                        )
                )

                .at(1, CompletionSupplier.startsWith(
                        //TODO: add check for vanished...
                        () -> sender.hasPermission("command.feed.other")
                                ? CommandUtil.getVisiblePlayers(sender).stream().map(Player::getName)
                                : Stream.empty()
                        )
                )
                .complete(args);
    }

    private void applyConvenienceAliases(List<String> args, boolean rewriteLastArgument) {
        if (args.size() >= 1 && (rewriteLastArgument || args.size() >= 2)) {
            replaceArgs(args, 0, arg -> switch (arg) {
                case "s", "0" -> "survival";
                case "c", "1" -> "creative";
                case "a", "2" -> "adventure";
                case "sp", "3" -> "spectator";
                default -> null; // removes element
            });
        }
    }
}
