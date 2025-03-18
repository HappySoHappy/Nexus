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

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
String stringMessage = PlainTextComponentSerializer.plainText().serialize(message);
                    stringMessage = translateLegacyFormatting(stringMessage);

                    Component messageComponent = miniMessage.deserialize(stringMessage, resolver);
 */

public class AsyncChatListener implements Listener {
    private static final Map<String, String> LEGACY_REPLACEMENTS = new HashMap<>() {{
        put("0", "<black>");
        put("1", "<dark_blue>");
        put("2", "<dark_green>");
        put("3", "<dark_aqua>");
        put("4", "<dark_red>");
        put("5", "<dark_purple>");
        put("6", "<gold>");
        put("7", "<gray>");
        put("8", "<dark_gray>");
        put("9", "<blue>");
        put("a", "<green>");
        put("b", "<aqua>");
        put("c", "<red>");
        put("d", "<light_purple>");
        put("e", "<yellow>");
        put("f", "<white>");
        put("k", "<magic>");
        put("l", "<bold>");
        put("m", "<strikethrough>");
        put("n", "<underline>");
        put("o", "<italic>");
        put("r", "<reset>");
    }};

    //TODO: it is possible to use translatables here, but they will not render properly to the sender.
    @EventHandler
    public void onChatRendering(AsyncChatEvent event) {
        event.renderer((player, playerDisplayName, message, audience) -> {
            IntegrationManager integrationManager = NexusPlugin.getInstance().getIntegrationManager();

            String primaryGroup = integrationManager.getLuckPerms().getPrimaryGroup(player);
            NexusConfiguration configuration = getGroupConfiguration(primaryGroup);

            if (!configuration.chatUseFormat()) {
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
                if (!messageFormat.isEmpty()) {
                    String formatWithPlaceholders = integrationManager.getPlaceholderApi().setPlaceholders(player, messageFormat);
                    Component formatComponent = MiniMessage.miniMessage().deserialize(formatWithPlaceholders);

                    //force to use color only!
                    //before we translate legacy, we need to check if we actually need to
                    String stringMessage = PlainTextComponentSerializer.plainText().serialize(message);

                    PermissionAwareMiniMessage pamm = PermissionAwareMiniMessage.legacyInstance();
                    Component messageComponent = formatComponent.replaceText(builder -> builder
                            .matchLiteral("%message%")
                            .replacement(
                                    pamm.deserialize(player, stringMessage)));

                    //or maybe remove unparsed formatting?

                    /*
                    List<NexusConfiguration.ColorReplacement> replacements = configuration.colorReplacement().getColorReplacements();
                    for (NexusConfiguration.ColorReplacement replacement : replacements) {
                        for (String pattern : replacement.patterns()) {
                            messageComponent = messageComponent.replaceText(builder ->
                                    builder.match(pattern).replacement(MiniMessage.miniMessage().deserialize(replacement.replacement())));

                        }
                    }
                    */

                    if (appendSpace) componentBuilder.appendSpace();
                    componentBuilder.append(messageComponent);
                }

                return componentBuilder.build();
            }

            String chatFormat = configuration.chatFormat();
            String formatWithPlaceholders =
                    integrationManager.getPlaceholderApi().setPlaceholders(player, chatFormat);

            Component chatComponent = LegacyComponentSerializer.legacy('&').deserialize(formatWithPlaceholders);

            return chatComponent
                    .replaceText(builder -> builder.matchLiteral("%player%").replacement(player.displayName()))
                    .replaceText(builder -> builder.matchLiteral("%message%").replacement(message));
        });
    }

    @EventHandler
    public void onChatReplacing(AsyncChatEvent event) {
        IntegrationManager integrationManager = NexusPlugin.getInstance().getIntegrationManager();
        String primaryGroup = integrationManager.getLuckPerms().getPrimaryGroup(event.getPlayer());
        NexusConfiguration configuration = getGroupConfiguration(primaryGroup);

        Component message = event.message();

        message = message.replaceText(b -> b.match("HELLO").replacement(MiniMessage.miniMessage().deserialize("<rainbow>HELLO WORLD OF RAINBOW")));

        event.message(message);

        /*Component message = event.message();
        List<NexusConfiguration.ChatReplacement> replacements = configuration.chatReplacement().getChatReplacements();
        for (NexusConfiguration.ChatReplacement replacement : replacements) {
            for (@RegExp String pattern : replacement.patterns()) {
                String originalMessage = MiniMessage.miniMessage().serialize(message);

                Component replacementComponent = MiniMessage.miniMessage().deserialize(replacement.replacement());

                System.out.println("oM: " + originalMessage + " replacement: " + MiniMessage.miniMessage().serialize(replacementComponent));
                message = Component.text(originalMessage).replaceText(b -> b.match(pattern).replacement(replacementComponent));

                String editedMessage = MiniMessage.miniMessage().serialize(message);
                System.out.println("eM: " + editedMessage);

                //originalMessage = originalMessage.replace(pattern, replacement.replacement());
                //message = Component.text(originalMessage);

                event.message(message);
            }
        }*/

        //event.message(message);
    }

    @EventHandler
    public void onChatFiltering(AsyncChatEvent event) {
        IntegrationManager integrationManager = NexusPlugin.getInstance().getIntegrationManager();
        String primaryGroup = integrationManager.getLuckPerms().getPrimaryGroup(event.getPlayer());
        NexusConfiguration configuration = getGroupConfiguration(primaryGroup);

        Component message = event.message();
        String originalMessage = PlainTextComponentSerializer.plainText().serialize(message);

        for (NexusConfiguration.FilterConfiguration.Filter filter : configuration.chatFilter().getFilters()) {
            for (String pattern : filter.patterns()) {
                Pattern compiledPattern = Pattern.compile(pattern, filter.strictCasing() ? 0 : Pattern.CASE_INSENSITIVE);
                Matcher matcher = compiledPattern.matcher(originalMessage);

                if (matcher.find()) {
                    switch (filter.action()) {
                        case CENSOR -> {
                            String censoredMessage = matcher.replaceAll(NexusConfiguration.FilterConfiguration.CENSOR_CHAR.repeat(matcher.group().length()));
                            message = Component.text(censoredMessage);
                        }
                        case REPLACE -> {
                            if (filter.replacement() != null) {
                                String replacedMessage = matcher.replaceAll(filter.replacement());
                                message = Component.text(replacedMessage);
                            }
                        }
                        case DENY -> {
                            event.setCancelled(true);
                            return;
                        }
                        case GHOST -> {
                            event.viewers().clear(); // Message won't be seen by others
                            event.viewers().add(Bukkit.getConsoleSender());
                            event.viewers().add(event.getPlayer());
                        }
                        case NOTIFY -> {
                            event.getPlayer().sendMessage(Component.text("Your message contains a filtered word."));
                        }
                    }
                    event.message(message);
                }
            }
        }
    }

    private NexusConfiguration getGroupConfiguration(String group) {
        return NexusPlugin.getGroupConfiguration().getOrDefault(group, NexusPlugin.getConfiguration());
    }

    /* https://gist.github.com/kezz/ff1bcb8c8db4e113e0119c210026d5ad
    public @NotNull TagResolver papiTag(final @NotNull Player player) {
    return TagResolver.resolver("papi", (argumentQueue, context) -> {
        // Get the string placeholder that they want to use.
        final String papiPlaceholder = argumentQueue.popOr("papi tag requires an argument").value();

        // Then get PAPI to parse the placeholder for the given player.
        final String parsedPlaceholder = PlaceholderAPI.setPlaceholders(player, '%' + papiPlaceholder + '%');

        // We need to turn this ugly legacy string into a nice component.
        final Component componentPlaceholder = LegacyComponentSerializer.legacySection().deserialize(parsedPlaceholder);

        // Finally, return the tag instance to insert the placeholder!
        return Tag.selfClosingInserting(componentPlaceholder);
    });
}
     */

    /*public class PermissionAwareMiniMessage {
        private final MiniMessage miniMessage;

        public PermissionAwareMiniMessage(Player player) {
            miniMessage = MiniMessage.builder()
                    .tags(TagResolver.builder()
                            .resolver(player.hasPermission("chatformatter.color.*")
                                    ? StandardTags.color()
                                    : TagResolver.empty())
                            .resolver(player.hasPermission("chatformatter.decorations.*")
                                    ? StandardTags.decorations()
                                    : TagResolver.empty())
                            .build()
                    )
                    .build();
        }

        public Component parse(String message) {
            return miniMessage.deserialize(message);
        }
    }*/
}
