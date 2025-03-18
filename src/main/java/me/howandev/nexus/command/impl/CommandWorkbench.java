package me.howandev.nexus.command.impl;

import me.howandev.nexus.command.Argument;
import me.howandev.nexus.command.CommandUtil;
import me.howandev.nexus.command.SimpleCommand;
import me.howandev.nexus.command.sender.Sender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.howandev.nexus.locale.Message.*;

public class CommandWorkbench extends SimpleCommand {
    public static final String USE_SELF = "command.workbench.self";
    public static final String USE_OTHER = "command.workbench.other";
    public CommandWorkbench() {
        super("workbench", List.of("crafting"), "description", "command.workbench");
    }

    @Override
    public boolean specifiesSyncExecution() {
        return true;
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
                if (!sender.hasPermission(USE_SELF) || sender.isConsole()) {
                    return;
                }

                Player player = Bukkit.getPlayer(sender.getUniqueId());
                if (player == null || !player.isOnline()) {
                    return;
                }

                InventoryView view = player.openWorkbench(null, true);
                if (view == null) {
                    plugin.getLogger().info("workbench view was null.");
                    return;
                }

                //COMMAND_HEAL_SELF.send(sender);
                plugin.getLogger().info(sender.getName() + " opened an workbench");
            }

            case 1 -> {
                if (!sender.hasPermission(USE_OTHER)) {
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

                InventoryView view = target.openWorkbench(null, true);
                if (view == null) {
                    plugin.getLogger().info("workbench view was null.");
                    return;
                }

                //COMMAND_HEAL_OTHER.send(sender, target.getName());
                plugin.getLogger().info((sender.isConsole() ? "(CONSOLE)" : sender.getName()) + " opened a workbench for " + target.getName());
            }
        }
    }
}
