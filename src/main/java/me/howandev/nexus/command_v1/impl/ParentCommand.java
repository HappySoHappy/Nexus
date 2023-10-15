package me.howandev.nexus.command_v1.impl;

import me.howandev.nexus.command.sender.Sender;
import me.howandev.nexus.command.tab.CompletionSupplier;
import me.howandev.nexus.command.tab.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

public abstract class ParentCommand<T, I> extends Command<Void> {
    /** Child sub commands */
    private final List<Command<T>> children;
    /** The type of parent command_v1 */
    private final Type type;

    public ParentCommand(String name, Type type, List<Command<T>> children) {
        super(name, null, integer -> false);
        this.children = children;
        this.type = type;
    }

    public @NotNull List<Command<T>> getChildren() {
        return this.children;
    }

    @Override
    public void execute(JavaPlugin plugin, Sender sender, Void ignored, List<String> args, String label) {
        // check if required argument and/or subcommand is missing
        if (args.size() < this.type.minArgs) {
            sendUsage(sender, label);
            return;
        }

        Command<T> sub = getChildren().stream()
                .filter(s -> s.getName().equalsIgnoreCase(args.get(this.type.cmdIndex)))
                .findFirst()
                .orElse(null);

        if (sub == null) {
            //Message.COMMAND_NOT_RECOGNISED.send(sender);
            return;
        }

        if (!sub.isAuthorized(sender)) {
            //Message.COMMAND_NO_PERMISSION.send(sender);
            return;
        }

        if (sub.getArgumentCheck().test(args.size() - this.type.minArgs)) {
            sub.sendDetailedUsage(sender, label);
            return;
        }

        final String targetArgument = args.get(0);
        I targetId = null;
        if (this.type == Type.TAKES_ARGUMENT_FOR_TARGET) {
            targetId = parseTarget(targetArgument, plugin, sender);
            if (targetId == null) {
                return;
            }
        }

        ReentrantLock lock = getLockForTarget(targetId);
        lock.lock();
        try {
            T target = getTarget(targetId, plugin, sender);
            if (target == null) {
                return;
            }

            try {
                sub.execute(plugin, sender, target, args.subList(this.type.minArgs, args.size()), label);
            } catch (Exception e) {
                //e.handle(sender, label, sub);
            }

            cleanup(target, plugin);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public List<String> tabComplete(JavaPlugin plugin, Sender sender, List<String> args) {
        return switch (this.type) {
            case TAKES_ARGUMENT_FOR_TARGET -> TabCompleter.create()
                    .at(0, CompletionSupplier.startsWith(() -> getTargets(plugin).stream()))
                    .at(1, CompletionSupplier.startsWith(() -> getChildren().stream()
                            .filter(s -> s.isAuthorized(sender))
                            .map(s -> s.getName().toLowerCase(Locale.ROOT))
                    ))
                    .from(2, partial -> getChildren().stream()
                            .filter(s -> s.isAuthorized(sender))
                            .filter(s -> s.getName().equalsIgnoreCase(args.get(1)))
                            .findFirst()
                            .map(cmd -> cmd.tabComplete(plugin, sender, args.subList(2, args.size())))
                            .orElse(Collections.emptyList())
                    )
                    .complete(args);

            case NO_TARGET_ARGUMENT -> TabCompleter.create()
                    .at(0, CompletionSupplier.startsWith(() -> getChildren().stream()
                            .filter(s -> s.isAuthorized(sender))
                            .map(s -> s.getName().toLowerCase(Locale.ROOT))
                    ))
                    .from(1, partial -> getChildren().stream()
                            .filter(s -> s.isAuthorized(sender))
                            .filter(s -> s.getName().equalsIgnoreCase(args.get(0)))
                            .findFirst()
                            .map(cmd -> cmd.tabComplete(plugin, sender, args.subList(1, args.size())))
                            .orElse(Collections.emptyList())
                    )
                    .complete(args);
        };
    }

    @Override
    public void sendUsage(Sender sender, String label) {
        List<Command<T>> subs = getChildren().stream()
                .filter(s -> s.isAuthorized(sender)).toList();

        if (!subs.isEmpty()) {
            //Message.MAIN_COMMAND_USAGE_HEADER.send(sender, getName(), String.format(getUsage(), label));
            sender.sendMessage("Usage Header");
            for (Command<?> s : subs) {
                s.sendUsage(sender, label);
            }
        } else {
            sender.sendMessage("No permission");
            //Message.COMMAND_NO_PERMISSION.send(sender);
        }
    }

    @Override
    public void sendDetailedUsage(Sender sender, String label) {
        sendUsage(sender, label);
    }

    @Override
    public boolean isAuthorized(Sender sender) {
        return getChildren().stream().anyMatch(sc -> sc.isAuthorized(sender));
    }

    protected abstract List<String> getTargets(JavaPlugin plugin);

    protected abstract I parseTarget(String target, JavaPlugin plugin, Sender sender);

    protected abstract ReentrantLock getLockForTarget(I target);

    protected abstract T getTarget(I target, JavaPlugin plugin, Sender sender);

    protected abstract void cleanup(T t, JavaPlugin plugin);

    public enum Type {
        // e.g. /lp log sub-command_v1....
        NO_TARGET_ARGUMENT(0),
        // e.g. /lp user <USER> sub-command_v1....
        TAKES_ARGUMENT_FOR_TARGET(1);

        private final int cmdIndex;
        private final int minArgs;

        Type(int cmdIndex) {
            this.cmdIndex = cmdIndex;
            this.minArgs = cmdIndex + 1;
        }
    }
}
