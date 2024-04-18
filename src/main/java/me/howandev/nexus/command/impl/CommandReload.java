package me.howandev.nexus.command.impl;

import me.howandev.nexus.NexusPlugin;
import me.howandev.nexus.command.Argument;
import me.howandev.nexus.command.ChildCommand;
import me.howandev.nexus.command.sender.Sender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandReload extends ChildCommand<Void> {
    public CommandReload() {
        super("reload", "", "command.reload");
    }

    @Override
    public Optional<List<Argument>> getArguments() {
        return Optional.empty();
    }

    @Override
    public void execute(JavaPlugin plugin, Sender sender, Void target, List<String> args, String label) throws Exception {
        long start = System.currentTimeMillis();
        NexusPlugin.getInstance().setupConfiguration();
        sender.sendMessage("success reload - took ("+(System.currentTimeMillis() - start)+"ms)");
    }
}
