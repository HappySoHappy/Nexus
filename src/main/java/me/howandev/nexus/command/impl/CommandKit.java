package me.howandev.nexus.command.impl;

import me.howandev.nexus.NexusPlugin;
import me.howandev.nexus.command.Argument;
import me.howandev.nexus.command.SimpleCommand;
import me.howandev.nexus.command.sender.Sender;
import me.howandev.nexus.kit.Kit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandKit extends SimpleCommand {

    public CommandKit() {
        super("kit", "command.kit.description", "command.kit");
    }

    @Override
    public boolean specifiesSyncExecution() {
        return true;
    }

    @Override
    public Optional<List<Argument>> getArguments() {
        List<Argument> args = new ArrayList<>();
        args.add(new Argument("kit", "kit"));
        args.add(new Argument("kitname", "kitname"));
        return Optional.of(args);
    }

    @Override
    public void execute(JavaPlugin plugin, Sender sender, List<String> args, String label) throws Exception {
        String kitName = args.get(0);

        Player player = Bukkit.getPlayer(sender.getUniqueId());
        if (player == null || !player.isOnline()) {
            return;
        }

        /*if (kitName.equalsIgnoreCase("createfrominv")) {
            kitName = args.get(1);
            var kit = new Kit(kitName);
            for (ItemStack item : player.getInventory().getContents()) {
                kit.addItem(item);
            }

            NexusPlugin.getInstance().getKitsManager().createKit(kit);
            NexusPlugin.getInstance().getKitsManager().saveKits();
        } else {
            var kit = NexusPlugin.getInstance().getKitsManager().getKit(kitName);
            if (kit != null) {
                for (ItemStack item : kit.getItems()) {
                    player.getInventory().addItem(item);
                }
            }
        }*/
    }
}
