package me.howandev.nexus.command.manager;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import me.howandev.nexus.command.sender.BukkitSenderFactory;
import me.howandev.nexus.command.Command;
import me.howandev.nexus.command.impl.CommandFeed;
import me.howandev.nexus.command.impl.CommandGamemode;
import me.howandev.nexus.command.impl.CommandHeal;
import me.howandev.nexus.command.sender.Sender;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

import static me.howandev.nexus.locale.Message.*;

@Getter
public class BukkitCommandManager implements Listener {
    private final JavaPlugin plugin;
    private final BukkitSenderFactory senderFactory;
    private final Map<String, Command<?>> mainCommands;
    private final AtomicBoolean executingCommand = new AtomicBoolean(false);
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("async-command-executor")
            .build()
    );
    public BukkitCommandManager(JavaPlugin plugin, BukkitSenderFactory senderFactory) {
        this.plugin = plugin;
        this.senderFactory = senderFactory;
        this.mainCommands = ImmutableList.<Command<?>>builder()
                .add(new CommandFeed())
                .add(new CommandGamemode())
                .add(new CommandHeal())
                .build()
                .stream()
                .collect(Collectors.toMap(c -> c.getName().toLowerCase(Locale.ROOT), Function.identity()));
    }

    public void registerAll() {
        mainCommands.values().forEach(this::register);
    }

    public boolean register(Command<?> command) {
        CommandMap commandMap = Bukkit.getCommandMap();
        return commandMap.register(command.getName(), plugin.getName(), wrap(command));
    }

    public CompletableFuture<Void> executeCommandAsync(Sender sender, Command<?> command, List<String> args) {
        return CompletableFuture.runAsync(() -> executeCommand(sender, command, args), executor);
    }

    public void executeCommand(Sender sender, Command<?> command, List<String> args) {
        if (command.specifiesConsoleOnly() && !sender.isConsole()) {
            COMMAND_CONSOLE_ONLY.send(sender, command.getName());
            return;
        }

        if (!command.isAuthorized(sender)) {
            COMMAND_MISSING_PERMISSION.send(sender, command.getName());
            return;
        }

        if (!command.getArgumentCheck().test(args.size())) {
            command.sendUsage(sender, command.getName());
            return;
        }

        executingCommand.set(true);
        try {
            command.execute(plugin, sender, null, args, command.getName());
        } catch (Throwable ex) {
            COMMAND_UNEXPECTED_EXCEPTION.send(sender);
            plugin.getLogger().warning(String.format(
                    "An unexpected exception was thrown while executing command '%s' with arguments '%s' issued by: '%s'",
                    command, args, sender
            ));
            ex.printStackTrace();
        } finally {
            executingCommand.set(false);
        }
    }

    private BukkitCommand wrap(Command<?> command) {
        return new BukkitCommand(command.getName(), PlainTextComponentSerializer.plainText().serialize(command.getDescription()), "N/A", command.getAliases()) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String[] args) {
                if (command.specifiesSyncExecution()) {
                    // Run sync on bukkit executor, with mutable args
                    executeCommand(senderFactory.wrap(sender), command, new ArrayList<>(List.of(args)));
                    return true;
                }

                // Run async on our executor, with mutable args
                executeCommandAsync(senderFactory.wrap(sender), command, new ArrayList<>(List.of(args)));
                return true;
            }

            @Override
            public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) throws IllegalArgumentException {
                // We are wrapping args into a new List, so it's mutable.
                return command.tabComplete(plugin, senderFactory.wrap(sender), new ArrayList<>(List.of(args)));
            }
        };
    }


    @EventHandler
    public void onCommandSending(PlayerCommandSendEvent ev) {
        for (Map.Entry<String, Command<?>> entry : mainCommands.entrySet()) {
            Command<?> command = entry.getValue();
            Sender sender = senderFactory.wrap(ev.getPlayer());
            if (!command.isAuthorized(sender) || !command.shouldDisplay()) {
                //todo: aliases and namespace
                ev.getCommands().remove(entry.getKey());
            }
        }
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
}
