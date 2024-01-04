package me.howandev.nexus.command.impl;

import com.google.common.collect.ImmutableList;
import me.howandev.nexus.command.ChildCommand;
import me.howandev.nexus.command.ParentCommand;
import me.howandev.nexus.command.sender.Sender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CommandTestParent extends ParentCommand<Void, Void> {
    public CommandTestParent() {
        super("testparent", "desc.testparent", "command.testparent", Type.NO_ARGUMENT_FOR_TARGET, ImmutableList.<ChildCommand<Void>>builder()
                .add(new CommandReload())
                .build()
                .stream()
                .toList()
        );
    }

    @Override
    public boolean specifiesSyncExecution() {
        return true;
    }

    @Override
    public Void parseTarget(String target, JavaPlugin plugin, Sender sender) {
        throw new UnsupportedOperationException(); // Never called for NO ARGUMENT FOR TARGET
    }

    @Override
    public Void getTarget(Void target, JavaPlugin plugin, Sender sender) {
        return target;
    }

    @Override
    protected List<String> getTargets(JavaPlugin plugin) {
        throw new UnsupportedOperationException(); // Never called for NO ARGUMENT FOR TARGET
    }
}
