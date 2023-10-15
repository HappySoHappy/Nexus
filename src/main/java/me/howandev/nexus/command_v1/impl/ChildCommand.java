package me.howandev.nexus.command_v1.impl;

import me.howandev.nexus.command.Argument;
import me.howandev.nexus.command.sender.Sender;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Abstract SubCommand class
 */
public abstract class ChildCommand<T> extends Command<T> {
    public ChildCommand(String name, String permission, Predicate<Integer> argumentCheck) {
        super(name, permission, argumentCheck);
    }

    /**
     * Send the command_v1 usage to a sender
     *
     * @param sender the sender to send the usage to
     */
    @Override
    public void sendUsage(Sender sender, String label) {
        TextComponent.Builder builder = Component.text()
                .append(Component.text('>', NamedTextColor.DARK_AQUA))
                .append(Component.space())
                .append(Component.text(getName().toLowerCase(Locale.ROOT), NamedTextColor.GREEN));

        if (getArgs().isPresent()) {
            List<Component> argUsages = getArgs().get().stream()
                    .map(Argument::asPrettyString)
                    .collect(Collectors.toList());

            builder.append(Component.text(" - ", NamedTextColor.DARK_AQUA))
                    .append(Component.join(JoinConfiguration.separator(Component.space()), argUsages))
                    .build();
        }

        sender.sendMessage(builder.build());
    }

    @Override
    public void sendDetailedUsage(Sender sender, String label) {
        //Message.COMMAND_USAGE_DETAILED_HEADER.send(sender, getName(), getDescription());
        sender.sendMessage("detailed header");
        if (getArgs().isPresent()) {
            sender.sendMessage("detailed args header");
            //Message.COMMAND_USAGE_DETAILED_ARGS_HEADER.send(sender);
            for (Argument arg : getArgs().get()) {
                sender.sendMessage("detailed arg");
                //Message.COMMAND_USAGE_DETAILED_ARG.send(sender, arg.asPrettyString(), arg.getDescription());
            }
        }
    }

}
