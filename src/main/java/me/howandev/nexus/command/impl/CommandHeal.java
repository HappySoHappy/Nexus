package me.howandev.nexus.command.impl;

import me.howandev.nexus.command.Argument;
import me.howandev.nexus.command.CommandUtil;
import me.howandev.nexus.command.SimpleCommand;
import me.howandev.nexus.command.sender.Sender;
import me.howandev.nexus.command.tab.CompletionSupplier;
import me.howandev.nexus.command.tab.TabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static me.howandev.nexus.locale.Message.*;

public class CommandHeal extends SimpleCommand {
    public CommandHeal() {
        super("heal", "command.heal.description", "command.heal");
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
                if (!sender.hasPermission("command.heal.self") || sender.isConsole()) {
                    return;
                }

                Player player = Bukkit.getPlayer(sender.getUniqueId());
                if (player == null || !player.isOnline()) {
                    return;
                }

                heal(player);
                COMMAND_HEAL_SELF.send(sender);
                plugin.getLogger().info(sender.getName() + " was healed");
            }

            case 1 -> {
                if (!sender.hasPermission("command.heal.other")) {
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

                heal(target);
                COMMAND_HEAL_OTHER.send(sender, target.getName());
                plugin.getLogger().info((sender.isConsole() ? "(CONSOLE)" : sender.getName()) + " healed " + target.getName());
            }
        }
    }

    private void heal(Player player) {
        double health = 20;
        AttributeInstance healthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (healthAttribute != null) {
            health = healthAttribute.getValue();
        }

        player.setHealth(health);
    }

    @Override
    public List<String> tabComplete(JavaPlugin plugin, Sender sender, List<String> args) {
        return TabCompleter.create()
                .at(0, CompletionSupplier.startsWith(
                                () -> sender.hasPermission("command.heal.other")
                                        ? CommandUtil.getVisiblePlayers(sender).stream().map(Player::getName)
                                        : Stream.empty()
                        )
                )
                .complete(args);
    }
}

