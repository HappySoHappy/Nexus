package me.howandev.nexus.command.impl;

import me.howandev.nexus.command.Argument;
import me.howandev.nexus.command.SimpleCommand;
import me.howandev.nexus.command.sender.Sender;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Optional;

public class CommandHat extends SimpleCommand {
    public CommandHat() {
        super("hat", "description", "command.hat");
    }

    @Override
    public boolean specifiesSyncExecution() {
        return true;
    }

    @Override
    public Optional<List<Argument>> getArguments() {
        return Optional.empty();
    }

    @Override
    public void execute(JavaPlugin plugin, Sender sender, List<String> args, String label) throws Exception {
        if (sender.isConsole()) {
            return;
        }

        Player player = Bukkit.getPlayer(sender.getUniqueId());
        if (player == null || !player.isOnline()) {
            return;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack helmet = inventory.getHelmet();
        ItemStack held = inventory.getItemInMainHand();
        int firstEmptySlot = inventory.firstEmpty(); // -1 if not found
        if (firstEmptySlot != -1 && helmet != null && !helmet.isEmpty()) {
            if (held.isEmpty()) {
                inventory.setItemInMainHand(helmet);
            } else {
                inventory.addItem(helmet);
            }

            inventory.setHelmet(null);
            plugin.getLogger().info(sender.getName() + " took hat off their head x" + helmet.getAmount() + " " + helmet.getType() + " named " + PlainTextComponentSerializer.plainText().serialize(helmet.displayName()) );
            return;
        }

        if (helmet == null || helmet.isEmpty() && (inventory.getHeldItemSlot() > 0 && !held.isEmpty())) {
            inventory.setHelmet(held);
            inventory.setItemInMainHand(null);

            plugin.getLogger().info(sender.getName() + " put a hat on their head x" + held.getAmount() + " " + held.getType() + " named " + PlainTextComponentSerializer.plainText().serialize(held.displayName()) );
        }
    }
}
