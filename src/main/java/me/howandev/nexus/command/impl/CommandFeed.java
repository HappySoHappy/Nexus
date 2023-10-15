package me.howandev.nexus.command.impl;

import me.howandev.nexus.command.Argument;
import me.howandev.nexus.command.CommandUtil;
import me.howandev.nexus.command.SingleCommand;
import me.howandev.nexus.command.sender.Sender;
import me.howandev.nexus.command.tab.CompletionSupplier;
import me.howandev.nexus.command.tab.TabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static me.howandev.nexus.locale.Message.*;

public class CommandFeed extends SingleCommand {
    public CommandFeed() {
        super("feed", "command.feed.description", "command.feed");
    }

    @Override
    public Optional<List<Argument>> getArguments() {
        List<Argument> args = new ArrayList<>();
        args.add(new Argument("argument.player", "argument.description.player", false));
        return Optional.of(args);
    }

    @Override
    public void execute(JavaPlugin plugin, Sender sender, List<String> args, String label) throws Exception {
        switch (args.size()) {
            case 0 -> {
                if (!sender.hasPermission("command.feed.self") || sender.isConsole()) {
                    return;
                }

                Player player = Bukkit.getPlayer(sender.getUniqueId());
                if (player == null || !player.isOnline()) {
                    return;
                }

                feed(player);
                COMMAND_FEED_SELF.send(sender);
                plugin.getLogger().info("Filled " + sender.getName() + "'s food and saturation level");
            }

            case 1 -> {
                if (!sender.hasPermission("command.feed.other")) {
                    return;
                }

                String targetArgument = args.get(0);
                Player target = CommandUtil.getVisiblePlayer(sender, targetArgument);
                if (target == null || !target.isOnline()) {
                    SEARCH_PLAYER_NOT_FOUND.send(sender);
                    return;
                }

                if (target.getUniqueId() == sender.getUniqueId()) {
                    return;
                }

                feed(target);
                COMMAND_FEED_OTHER.send(sender, target.getName());
                plugin.getLogger().info((sender.isConsole() ? "(CONSOLE)" : sender.getName()) + " filled " + target.getName() + "'s food and saturation level");
            }
        }
    }

    private void feed(Player player) {
        player.setExhaustion(0F);
        player.setFoodLevel(20);
        player.setSaturation(10);
    }

    @Override
    public List<String> tabComplete(JavaPlugin plugin, Sender sender, List<String> args) {
        return TabCompleter.create()
                .at(0, CompletionSupplier.startsWith(
                                () -> sender.hasPermission("command.feed.other")
                                        ? CommandUtil.getVisiblePlayers(sender).stream().map(Player::getName)
                                        : Stream.empty()
                        )
                )
                .complete(args);
    }
}
