package me.howandev.nexus.command.manager;

import com.google.common.collect.ImmutableList;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Getter;
import me.howandev.nexus.command.ParentCommand;
import me.howandev.nexus.command.impl.*;
import me.howandev.nexus.command.sender.BukkitSenderFactory;
import me.howandev.nexus.command.Command;
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
    private final Map<String, Command<?>> commands;
    // Used when running async commands which need to be run in order
    private final AtomicBoolean executingCommand = new AtomicBoolean(false);
    private final ExecutorService executor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder()
            .setDaemon(true)
            .setNameFormat("async-command-executor")
            .build()
    );
    public BukkitCommandManager(JavaPlugin plugin, BukkitSenderFactory senderFactory) {
        this.plugin = plugin;
        this.senderFactory = senderFactory;
        this.commands = ImmutableList.<Command<?>>builder()
                .add(new CommandFeed())
                .add(new CommandFly())
                .add(new CommandGamemode())
                .add(new CommandHeal())
                .add(new CommandSpeed())
                .add(new CommandTestParent())
                .build()
                .stream()
                .collect(Collectors.toMap(c -> c.getName().toLowerCase(Locale.ROOT), Function.identity()));
    }

    public void registerAll() {
        commands.values().forEach(this::register);
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
        Sender sender = senderFactory.wrap(ev.getPlayer());
        String pluginName = plugin.getName().toLowerCase(Locale.ENGLISH).trim();

        for (Map.Entry<String, Command<?>> entry : commands.entrySet()) {
            Command<?> command = entry.getValue();
            if (!command.isAuthorized(sender) || !command.shouldDisplay()) {
                ev.getCommands().remove(entry.getKey().toLowerCase(Locale.ENGLISH).trim());
                ev.getCommands().remove(pluginName+":"+entry.getKey().toLowerCase(Locale.ENGLISH).trim());
                for (String alias : command.getAliases()) {
                    ev.getCommands().remove(alias.toLowerCase(Locale.ENGLISH).trim());
                    ev.getCommands().remove(pluginName+":"+alias.toLowerCase(Locale.ENGLISH).trim());
                }
            }
        }
    }
}
