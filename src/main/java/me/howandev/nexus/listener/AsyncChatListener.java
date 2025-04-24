package me.howandev.nexus.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.howandev.nexus.NexusConfiguration;
import me.howandev.nexus.NexusPlugin;
import me.howandev.nexus.integration.IntegrationManager;
import me.howandev.nexus.util.PermissionAwareMiniMessage;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.intellij.lang.annotations.RegExp;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsyncChatListener implements Listener {
    //todo: can avoid filtering by abusing formatting such as: &6nig&6ger
    @EventHandler
    public void onChatFiltering(AsyncChatEvent event) {
        IntegrationManager integrationManager = NexusPlugin.getInstance().getIntegrationManager();
        String primaryGroup = integrationManager.getLuckPerms().getPrimaryGroup(event.getPlayer());

        NexusConfiguration configuration = getGroupConfiguration(primaryGroup);
        configuration = NexusPlugin.getConfiguration().merge(configuration);

        Component message = event.message();
        String originalMessage = PlainTextComponentSerializer.plainText().serialize(message);

        boolean modified = false;
        Component modifiedMessage = message;

        for (NexusConfiguration.FilterConfiguration.Filter filter : configuration.chatFilter().getFilters()) {
            for (String pattern : filter.patterns()) {
                Pattern compiledPattern = Pattern.compile(pattern, filter.strictCasing() ? 0 : Pattern.CASE_INSENSITIVE);
                Matcher matcher = compiledPattern.matcher(originalMessage);

                while (matcher.find()) {
                    String matchedText = matcher.group();

                    switch (filter.action()) {
                        case CENSOR -> {
                            String censorText = NexusConfiguration.FilterConfiguration.CENSOR_CHAR.repeat(matchedText.length());

                            modifiedMessage = modifiedMessage.replaceText(builder -> builder
                                    .matchLiteral(matchedText)
                                    .replacement(censorText));

                            modified = true;
                        }

                        case REPLACE -> {
                            int start = matcher.start();
                            int end = originalMessage.indexOf(" ", matcher.end());
                            if (end == -1) end = originalMessage.length();

                            String fullMatchedWord = originalMessage.substring(start, end);
                            String replacement = filter.replacement();

                            if (replacement.isEmpty()) {
                                boolean leadingSpace = start > 0 && originalMessage.charAt(start - 1) == ' ';
                                boolean trailingSpace = end < originalMessage.length() && originalMessage.charAt(end) == ' ';

                                if (leadingSpace && trailingSpace) {
                                    start -= 1; // remove one space before
                                    fullMatchedWord = originalMessage.substring(start, end);
                                }
                            }

                            String finalFullMatchedWord = fullMatchedWord;
                            modifiedMessage = modifiedMessage.replaceText(builder ->
                                    builder.matchLiteral(finalFullMatchedWord).replacement(replacement)
                            );

                            if (MiniMessage.miniMessage().serialize(modifiedMessage).isBlank()) {
                                event.setCancelled(true);
                                return;
                            }

                            modified = true;
                        }

                        case DENY -> {
                            event.setCancelled(true);
                            return;
                        }

                        case GHOST -> {
                            event.viewers().clear();
                            event.viewers().add(Bukkit.getConsoleSender());
                            event.viewers().add(event.getPlayer());

                            NexusPlugin.getPluginLogger().info("Ghosted " + event.getPlayer().getName() + "'s message because it contains '" + matchedText + "'");
                            return;
                        }
                    }
                }
            }
        }

        event.message(modifiedMessage);
        if (modified)
            NexusPlugin.getPluginLogger().info("Modified " + event.getPlayer().getName() + "'s message: " + originalMessage + " -> " + PlainTextComponentSerializer.plainText().serialize(event.message()));

    }

    //TODO: it is possible to use translatables here, but they will not render properly to the sender.
    @EventHandler
    public void onChatRendering(AsyncChatEvent event) {
        event.renderer((player, playerDisplayName, message, audience) -> {
            IntegrationManager integrationManager = NexusPlugin.getInstance().getIntegrationManager();

            String primaryGroup = integrationManager.getLuckPerms().getPrimaryGroup(player);

            NexusConfiguration configuration = getGroupConfiguration(primaryGroup);
            configuration = NexusPlugin.getConfiguration().merge(configuration);

            if (configuration.chatUseFormat()) {
                String chatFormat = configuration.chatFormat();
                String formatWithPlaceholders =
                        integrationManager.getPlaceholderApi().setPlaceholders(player, chatFormat);

                Component chatComponent = LegacyComponentSerializer.legacy('&').deserialize(formatWithPlaceholders);

                return chatComponent
                        .replaceText(builder -> builder.matchLiteral("%player%").replacement(player.displayName()))
                        .replaceText(builder -> builder.matchLiteral("%message%").replacement(message));
            }

            NexusConfiguration.RichFormatConfiguration richFormat = configuration.chatRichFormat();

            TextComponent.Builder componentBuilder = Component.text();
            boolean appendSpace = false;

            String prefixFormat = richFormat.prefix();
            if (!prefixFormat.isEmpty()) {
                String prefixFormatted = integrationManager.getPlaceholderApi().setPlaceholders(player, prefixFormat);
                // minimessage legacy serializer does not serialize <white>, thank you
                prefixFormatted = prefixFormatted.replace("&f", "<white>").replace("§f", "<white>");

                Component prefixComponentLegacy = LegacyComponentSerializer.legacySection().deserialize(prefixFormatted);
                String prefixSection = PlainTextComponentSerializer.plainText().serialize(prefixComponentLegacy);

                //todo: find a better way to translate legacy formatting
                prefixComponentLegacy = LegacyComponentSerializer.legacyAmpersand().deserialize(prefixSection);
                String prefixAmpersand = PlainTextComponentSerializer.plainText().serialize(prefixComponentLegacy);

                Component prefixComponent = MiniMessage.miniMessage().deserialize(prefixAmpersand);
                componentBuilder.append(prefixComponent);
                appendSpace = true;
            }

            String senderFormat = richFormat.sender();
            if (!senderFormat.isEmpty()) {
                String senderFormatted = integrationManager.getPlaceholderApi().setPlaceholders(player, senderFormat);
                Component senderComponent = MiniMessage.miniMessage().deserialize(senderFormatted)
                        .replaceText(builder -> builder.matchLiteral("%player%").replacement(player.getName()));

                if (appendSpace) componentBuilder.appendSpace();
                componentBuilder.append(senderComponent);
                appendSpace = true;
            }

            String suffixFormat = richFormat.suffix();
            if (!suffixFormat.isEmpty()) {
                String suffixFormatted = integrationManager.getPlaceholderApi().setPlaceholders(player, suffixFormat);
                Component suffixComponent = MiniMessage.miniMessage().deserialize(suffixFormatted);
                if (appendSpace) componentBuilder.appendSpace();
                componentBuilder.append(suffixComponent);
                appendSpace = true;
            }

            String separatorFormat = richFormat.separator();
            if (!separatorFormat.isEmpty()) {
                String separatorFormatted = integrationManager.getPlaceholderApi().setPlaceholders(player, separatorFormat);
                Component separatorComponent = MiniMessage.miniMessage().deserialize(separatorFormatted);
                if (appendSpace) componentBuilder.appendSpace();
                componentBuilder.append(separatorComponent);
                appendSpace = true;
            }

            String messageFormat = richFormat.message();
            if (messageFormat.isEmpty()) {
                return componentBuilder.build();
            }

            String formatWithPlaceholders = integrationManager.getPlaceholderApi().setPlaceholders(player, messageFormat);
            Component formatComponent = MiniMessage.miniMessage().deserialize(formatWithPlaceholders);

            String stringMessage = PlainTextComponentSerializer.plainText().serialize(message);

            // if you're wondering why you can use any color, check if you are an operator - PAMM doesn't consider explicit permissions!
            PermissionAwareMiniMessage pamm = PermissionAwareMiniMessage.legacyInstance();
            Component messageComponent = formatComponent.replaceText(builder -> builder
                    .matchLiteral("%message%")
                    .replacement(
                            pamm.deserialize(player, stringMessage)));

            List<NexusConfiguration.ChatReplacement> replacements = configuration.chatReplacement().getChatReplacements();
            for (NexusConfiguration.ChatReplacement replacement : replacements) {
                for (@RegExp String pattern : replacement.patterns()) {
                    if (!replacement.permission().isEmpty() && !integrationManager.getLuckPerms().hasPermission(player, replacement.permission())) {
                        continue;
                    }

                    Component replacementComponent = MiniMessage.miniMessage().deserialize(replacement.replacement());

                    messageComponent = messageComponent.replaceText(b -> b.match(pattern).replacement(replacementComponent));
                }
            }

            if (appendSpace) componentBuilder.appendSpace();
            componentBuilder.append(messageComponent);

            return componentBuilder.build();
        });
    }

    private NexusConfiguration getGroupConfiguration(String group) {
        return NexusPlugin.getGroupConfiguration().getOrDefault(group, NexusPlugin.getConfiguration());
    }
}
