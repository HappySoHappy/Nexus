package me.howandev.nexus.command;

import lombok.Getter;
import me.howandev.nexus.command.sender.Sender;
import me.howandev.nexus.command.tab.CompletionSupplier;
import me.howandev.nexus.command.tab.TabCompleter;
import me.howandev.nexus.locale.Message;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static me.howandev.nexus.locale.Message.*;

@Getter
public abstract class ParentCommand<T, I> extends ChildCommand<T> {
    private final Type type;
    private final List<ChildCommand<T>> children;
    public ParentCommand(@NotNull String name, String descriptionKey, @Nullable String permission, Type type, List<ChildCommand<T>> children) {
        super(name, descriptionKey, permission, integer -> true);
        this.type = type;
        this.children = children;
    }

    public ParentCommand(@NotNull String name, @NotNull List<String> aliases, String descriptionKey, @Nullable String permission, Type type, List<ChildCommand<T>> children) {
        super(name, aliases, descriptionKey, permission);
        this.type = type;
        this.children = children;
    }

    @Override
    public Optional<List<Argument>> getArguments() {
        List<Argument> args = new ArrayList<>();
        args.add(new Argument("subcommand", "subcommand"));
        return Optional.of(args);
    }

    @Override
    public void sendUsage(Sender sender, String label) {
        sender.sendMessage("usage parent");
    }

    @Override
    public void sendDetailedUsage(Sender sender, String label) {
        sender.sendMessage("detailed usage parent");
    }

    @Override
    public void execute(JavaPlugin plugin, Sender sender, T ignored, List<String> args, String label) throws Exception {
        if (args.size() < this.type.minArgs) {
            sendUsage(sender, label);
            return;
        }

        String commandLabel = args.get(this.type.cmdIndex);
        //check arg size to not get Index out of bounds
        Command<T> subcommand = getChildren().stream()
                .filter(s -> s.getName().equalsIgnoreCase(commandLabel))
                .findFirst()
                .orElse(null);

        if (subcommand == null) {
            COMMAND_UNKNOWN.send(sender, commandLabel);
            return;
        }

        args.remove(0); //FIXME: target argument will always be null this way!

        /*sender.sendMessage("Count: "+args.size()+" Check: "+subcommand.getArgumentCheck().test(args.size())+" Arguments:");
        for (String arg : args
             ) {
            sender.sendMessage("  - "+arg);
        }*/

        List<String> subArgs = args.subList(type.cmdIndex, args.size());
        /*sender.sendMessage("Count sublist: "+args1.size()+" Check: "+subcommand.getArgumentCheck().test(args1.size())+" Arguments:");
        for (String arg : args1
        ) {
            sender.sendMessage("  - "+arg);
        }*/

        if (!subcommand.getArgumentCheck().test(subArgs.size())) {
            subcommand.sendDetailedUsage(sender, label);
            return;
        }

        I targetId = null;
        if (this.type == Type.ARGUMENT_FOR_TARGET) {
            final String targetArgument = args.get(0);
            targetId = parseTarget(targetArgument, plugin, sender);
            if (targetId == null) {
                return;
            }
        }

        T target = getTarget(targetId, plugin, sender);
        if (target == null) {
            sender.sendMessage("target is null!");
            //return;
        }

        subcommand.execute(plugin, sender, target, subArgs, commandLabel);

        /*ReentrantLock lock = getLockForTarget(targetId);
        lock.lock();
        try {
            T target = getTarget(targetId, plugin, sender);
            if (target == null) {
                return;
            }

            try {
                subcommand.execute(plugin, sender, target, args.subList(this.type.minArgs, args.size()), label);
            } catch (Exception e) {
                //e.handle(sender, label, sub);
            }

            cleanup(target, plugin);
        } finally {
            lock.unlock();
        }*/
    }

    /*private void executeParent(Sender sender, String label, List<String> args) {
        // Handle no arguments
        if (args.isEmpty() || args.size() == 1 && args.get(0).trim().isEmpty()) {
            sender.sendMessage("handling no arguments");
            return;
        }

        // Look for the main command.
        Command<?> main = this.mainCommands.get(args.get(0).toLowerCase(Locale.ROOT));
        if (main == null) {
            sender.sendMessage("there is no main command by that name: "+args.get(0));
            return;
        }

        // Check the Sender has permission to use the main command.
        if (!main.isAuthorized(sender)) {
            sender.sendMessage("you are not authorized, allowing execution anyways");
            //return;
        }

        args.remove(0); // remove the main command arg.

        // Check the correct number of args were given for the main command
        if (main.getArgumentCheck().test(args.size())) {
            sender.sendMessage("invalid argument size");
            return;
        }

        // Try to execute the command
        try {
            main.execute(this.plugin, sender, null, args, label);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }*/

    @Override
    public List<String> tabComplete(JavaPlugin plugin, Sender sender, List<String> args) {
        return switch (this.type) {
            case ARGUMENT_FOR_TARGET -> TabCompleter.create()
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

            case NO_ARGUMENT_FOR_TARGET -> TabCompleter.create()
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

    public abstract I parseTarget(String target, JavaPlugin plugin, Sender sender);

    public abstract T getTarget(I target, JavaPlugin plugin, Sender sender);

    protected abstract List<String> getTargets(JavaPlugin plugin);

    public enum Type {
        /**
         * A command that doesn't require a target, e.g.:
         * {@literal /command help}
         */
        NO_ARGUMENT_FOR_TARGET(0),
        /**
         * A command that requires a target, e.g.:
         * {@literal /info <player> stats}
         */
        ARGUMENT_FOR_TARGET(1);

        private final int cmdIndex;
        private final int minArgs;

        Type(int cmdIndex) {
            this.cmdIndex = cmdIndex;
            this.minArgs = cmdIndex + 1;
        }
    }
}
