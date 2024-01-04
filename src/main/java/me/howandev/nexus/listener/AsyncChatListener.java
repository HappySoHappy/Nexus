package me.howandev.nexus.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.howandev.nexus.NexusConfiguration;
import me.howandev.nexus.NexusPlugin;
import me.howandev.nexus.integration.papi.PlaceholderApiIntegration;
import me.howandev.nexus.integration.vault.VaultIntegration;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AsyncChatListener implements Listener {
    //TODO: it is possible to use translatables here, but they will not render properly to the sender.
    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        event.renderer((player, playerDisplayName, message, audience) -> {
            NexusConfiguration configuration = NexusPlugin.getConfiguration();
            VaultIntegration vaultIntegration = NexusPlugin.getInstance().getIntegrationManager().vaultIntegration();

            String primaryGroup = vaultIntegration.getPrimaryGroup(player);
            if (primaryGroup != null) {
                NexusConfiguration groupConfiguration = NexusPlugin.getGroupConfiguration().get(primaryGroup);
                if (groupConfiguration != null)
                    configuration = groupConfiguration;
            }

            PlaceholderApiIntegration papiIntegration = NexusPlugin.getInstance().getIntegrationManager().papiIntegration();
            if (configuration.chatUseFormat()) {
                String chatFormat = configuration.chatFormat();

                String chatFormatted = papiIntegration.setPlaceholders(player, chatFormat);
                Component chatComponent = LegacyComponentSerializer.legacy('&').deserialize(chatFormatted);

                return chatComponent
                        .replaceText(builder -> builder.matchLiteral("%player%").replacement(player.displayName()))
                        .replaceText(builder -> builder.matchLiteral("%message%").replacement(message));
            }

            NexusConfiguration.RichFormatConfiguration richFormat = configuration.chatRichFormat();
            TextComponent.Builder componentBuilder = Component.text();
            boolean appendSpace = false;

            String prefixFormat = richFormat.prefix();
            if (!prefixFormat.isEmpty()) {
                String prefixFormatted = papiIntegration.setPlaceholders(player, prefixFormat);
                Component prefixComponent = MiniMessage.miniMessage().deserialize(prefixFormatted);
                componentBuilder.append(prefixComponent);
                appendSpace = true;
            }

            String senderFormat = richFormat.sender();
            if (!senderFormat.isEmpty()) {
                String senderFormatted = papiIntegration.setPlaceholders(player, senderFormat);
                Component senderComponent = MiniMessage.miniMessage().deserialize(senderFormatted)
                        .replaceText(builder -> builder.matchLiteral("%player%").replacement(playerDisplayName));

                if (appendSpace) componentBuilder.appendSpace();
                componentBuilder.append(senderComponent);
                appendSpace = true;
            }

            String suffixFormat = richFormat.suffix();
            if (!suffixFormat.isEmpty()) {
                String suffixFormatted = papiIntegration.setPlaceholders(player, suffixFormat);
                Component suffixComponent = MiniMessage.miniMessage().deserialize(suffixFormatted);
                if (appendSpace) componentBuilder.appendSpace();
                componentBuilder.append(suffixComponent);
                appendSpace = true;
            }

            String separatorFormat = richFormat.separator();
            if (!separatorFormat.isEmpty()) {
                String separatorFormatted = papiIntegration.setPlaceholders(player, separatorFormat);
                Component separatorComponent = MiniMessage.miniMessage().deserialize(separatorFormatted);
                if (appendSpace) componentBuilder.appendSpace();
                componentBuilder.append(separatorComponent);
                appendSpace = true;
            }

            String messageFormat = richFormat.message();
            if (!messageFormat.isEmpty()) {
                TagResolver resolver = TagResolver.builder().resolver(StandardTags.color()).build();

                String messageFormatted = papiIntegration.setPlaceholders(player, messageFormat);
                Component messageComponent = MiniMessage.miniMessage().deserialize(messageFormatted, resolver)
                        .replaceText(builder -> builder.matchLiteral("%message%").replacement(message));
                if (appendSpace) componentBuilder.appendSpace();
                componentBuilder.append(messageComponent);
            }

            //TODO: add custom serializer to allow certain tags usage and ignore others.
            // this allows us to create custom rules for groups etc... eg. :EMOTES:
            return componentBuilder.build();
        });
    }
}
