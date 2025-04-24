package me.howandev.nexus.util;

import com.google.common.collect.ImmutableMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.*;
import net.kyori.adventure.text.minimessage.Context;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.ParsingException;
import net.kyori.adventure.text.minimessage.internal.serializer.SerializableResolver;
import net.kyori.adventure.text.minimessage.internal.serializer.StyleClaim;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.ArgumentQueue;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.UnaryOperator;

public class PermissionAwareMiniMessage {
    private static final String PERMISSION_BASE = "nexus.chat.format";

    private static final Map<String, TagResolver> TAG_RESOLVERS_BY_PERMISSION = new ImmutableMap.Builder<String, TagResolver>()
            .put(PERMISSION_BASE + ".*", StandardTags.defaults())

            .put(PERMISSION_BASE + ".color.*", StandardTags.color())
            .put(PERMISSION_BASE + ".color.black", PerColorTagResolver.of(NamedTextColor.BLACK))
            .put(PERMISSION_BASE + ".color.dark_blue", PerColorTagResolver.of(NamedTextColor.DARK_BLUE))
            .put(PERMISSION_BASE + ".color.dark_green", PerColorTagResolver.of(NamedTextColor.DARK_GREEN))
            .put(PERMISSION_BASE + ".color.dark_aqua", PerColorTagResolver.of(NamedTextColor.DARK_AQUA))
            .put(PERMISSION_BASE + ".color.dark_red", PerColorTagResolver.of(NamedTextColor.DARK_RED))
            .put(PERMISSION_BASE + ".color.dark_purple", PerColorTagResolver.of(NamedTextColor.DARK_PURPLE))
            .put(PERMISSION_BASE + ".color.gold", PerColorTagResolver.of(NamedTextColor.GOLD))
            .put(PERMISSION_BASE + ".color.gray", PerColorTagResolver.of(NamedTextColor.GRAY))
            .put(PERMISSION_BASE + ".color.dark_gray", PerColorTagResolver.of(NamedTextColor.DARK_GRAY))
            .put(PERMISSION_BASE + ".color.blue", PerColorTagResolver.of(NamedTextColor.BLUE))
            .put(PERMISSION_BASE + ".color.green", PerColorTagResolver.of(NamedTextColor.GREEN))
            .put(PERMISSION_BASE + ".color.aqua", PerColorTagResolver.of(NamedTextColor.AQUA))
            .put(PERMISSION_BASE + ".color.red", PerColorTagResolver.of(NamedTextColor.RED))
            .put(PERMISSION_BASE + ".color.light_purple", PerColorTagResolver.of(NamedTextColor.LIGHT_PURPLE))
            .put(PERMISSION_BASE + ".color.yellow", PerColorTagResolver.of(NamedTextColor.YELLOW))
            .put(PERMISSION_BASE + ".color.white", PerColorTagResolver.of(NamedTextColor.WHITE))
            //todo: add hex color support too, StandardTags.Color() supports hex, but need individual color support too
            // with permissions like: ".color.custom", ".color.#FF00FF"

            .put(PERMISSION_BASE + ".decorations.*", StandardTags.decorations())
            .put(PERMISSION_BASE + ".decorations.bold", StandardTags.decorations(TextDecoration.BOLD))
            .put(PERMISSION_BASE + ".decorations.italic", StandardTags.decorations(TextDecoration.ITALIC))
            .put(PERMISSION_BASE + ".decorations.underlined", StandardTags.decorations(TextDecoration.UNDERLINED))
            .put(PERMISSION_BASE + ".decorations.strikethrough", StandardTags.decorations(TextDecoration.STRIKETHROUGH))
            .put(PERMISSION_BASE + ".decorations.obfuscated", StandardTags.decorations(TextDecoration.OBFUSCATED))

            .put(PERMISSION_BASE + ".reset", StandardTags.reset())
            .put(PERMISSION_BASE + ".gradient", StandardTags.gradient())
            .put(PERMISSION_BASE + ".hover", StandardTags.hoverEvent())
            .put(PERMISSION_BASE + ".click", StandardTags.clickEvent())
            .put(PERMISSION_BASE + ".insertion", StandardTags.insertion())
            .put(PERMISSION_BASE + ".font", StandardTags.font())
            .put(PERMISSION_BASE + ".transition", StandardTags.transition())
            .put(PERMISSION_BASE + ".translatable", StandardTags.translatable())
            .put(PERMISSION_BASE + ".selector", StandardTags.selector())
            .put(PERMISSION_BASE + ".keybind", StandardTags.keybind())
            .put(PERMISSION_BASE + ".newline", StandardTags.newline())
            .put(PERMISSION_BASE + ".rainbow", StandardTags.rainbow())
            .build();

    private static final MiniMessage EMPTY_TAG_MINIMESSAGE = MiniMessage.builder()
            .tags(TagResolver.empty())
            .build();

    public static PermissionAwareMiniMessage instance() {
        return LazyPammHolder.INSTANCE;
    }

    public static PermissionAwareLegacyMiniMessage legacyInstance() {
        return LazyLegacyPammHolder.INSTANCE;
    }

    private PermissionAwareMiniMessage() {

    }

    public @NotNull Component deserialize(@NotNull Player player, @NotNull String message) {
        return EMPTY_TAG_MINIMESSAGE.deserialize(message, collectPermittedTags(player));
    }

    public @NotNull String serialize(@NotNull Component message) {
        return EMPTY_TAG_MINIMESSAGE.serialize(message);
    }

    public @NotNull TagResolver collectPermittedTags(@NotNull Player player) {
        List<TagResolver> tagResolvers = new ArrayList<>();

        for (Map.Entry<String, TagResolver> entry : TAG_RESOLVERS_BY_PERMISSION.entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                tagResolvers.add(entry.getValue());
            }
        }

        return TagResolver.resolver(tagResolvers);
    }

    public static class PermissionAwareLegacyMiniMessage extends PermissionAwareMiniMessage {
        private static final Map<String, TagResolver> LEGACY_TAG_RESOLVERS_BY_PERMISSION = new ImmutableMap.Builder<String, TagResolver>()
                .put(PERMISSION_BASE + ".legacy.*", TagResolver.resolver(StandardTags.color(), StandardTags.decorations(), StandardTags.reset()))

                .put(PERMISSION_BASE + ".legacy.0", PerColorTagResolver.of(NamedTextColor.BLACK))
                .put(PERMISSION_BASE + ".legacy.1", PerColorTagResolver.of(NamedTextColor.DARK_BLUE))
                .put(PERMISSION_BASE + ".legacy.2", PerColorTagResolver.of(NamedTextColor.DARK_GREEN))
                .put(PERMISSION_BASE + ".legacy.3", PerColorTagResolver.of(NamedTextColor.DARK_AQUA))
                .put(PERMISSION_BASE + ".legacy.4", PerColorTagResolver.of(NamedTextColor.DARK_RED))
                .put(PERMISSION_BASE + ".legacy.5", PerColorTagResolver.of(NamedTextColor.DARK_PURPLE))
                .put(PERMISSION_BASE + ".legacy.6", PerColorTagResolver.of(NamedTextColor.GOLD))
                .put(PERMISSION_BASE + ".legacy.7", PerColorTagResolver.of(NamedTextColor.GRAY))
                .put(PERMISSION_BASE + ".legacy.8", PerColorTagResolver.of(NamedTextColor.DARK_GRAY))
                .put(PERMISSION_BASE + ".legacy.9", PerColorTagResolver.of(NamedTextColor.BLUE))
                .put(PERMISSION_BASE + ".legacy.a", PerColorTagResolver.of(NamedTextColor.GREEN))
                .put(PERMISSION_BASE + ".legacy.b", PerColorTagResolver.of(NamedTextColor.AQUA))
                .put(PERMISSION_BASE + ".legacy.c", PerColorTagResolver.of(NamedTextColor.RED))
                .put(PERMISSION_BASE + ".legacy.d", PerColorTagResolver.of(NamedTextColor.LIGHT_PURPLE))
                .put(PERMISSION_BASE + ".legacy.e", PerColorTagResolver.of(NamedTextColor.YELLOW))
                .put(PERMISSION_BASE + ".legacy.f", PerColorTagResolver.of(NamedTextColor.WHITE))

                .put(PERMISSION_BASE + ".legacy.l", StandardTags.decorations(TextDecoration.BOLD))
                .put(PERMISSION_BASE + ".legacy.o", StandardTags.decorations(TextDecoration.ITALIC))
                .put(PERMISSION_BASE + ".legacy.n", StandardTags.decorations(TextDecoration.UNDERLINED))
                .put(PERMISSION_BASE + ".legacy.m", StandardTags.decorations(TextDecoration.STRIKETHROUGH))
                .put(PERMISSION_BASE + ".legacy.k", StandardTags.decorations(TextDecoration.OBFUSCATED))
                .put(PERMISSION_BASE + ".legacy.r", StandardTags.reset())

                .build();

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

        private static final MiniMessage EMPTY_LEGACY_TAG_MINIMESSAGE = MiniMessage.builder()
                .tags(TagResolver.empty())
                .preProcessor(new LegacySectionTagPreProcessor())
                .build();

        private PermissionAwareLegacyMiniMessage() {

        }

        @Override
        public @NotNull Component deserialize(@NotNull Player player, @NotNull String message) {
            message = message.replace("&&", "&amp;");

            for (Map.Entry<String, String> entry : LEGACY_REPLACEMENTS.entrySet()) {
                if (player.hasPermission(PERMISSION_BASE + ".legacy." + entry.getKey())) {
                    message = message.replace("&" + entry.getKey(), entry.getValue());
                }
            }

            return EMPTY_LEGACY_TAG_MINIMESSAGE.deserialize(message, collectPermittedTags(player))
                    .replaceText(builder -> builder.matchLiteral("&amp;").replacement("&"));
        }

        @Override
        public @NotNull String serialize(@NotNull Component message) {
            return EMPTY_LEGACY_TAG_MINIMESSAGE.serialize(message);
        }

        @Override
        public @NotNull TagResolver collectPermittedTags(@NotNull Player player) {
            List<TagResolver> tagResolvers = new ArrayList<>();

            for (Map.Entry<String, TagResolver> entry : TAG_RESOLVERS_BY_PERMISSION.entrySet()) {
                if (player.hasPermission(entry.getKey())) {
                    tagResolvers.add(entry.getValue());
                }
            }

            for (Map.Entry<String, TagResolver> entry : LEGACY_TAG_RESOLVERS_BY_PERMISSION.entrySet()) {
                if (player.hasPermission(entry.getKey())) {
                    tagResolvers.add(entry.getValue());
                }
            }

            return TagResolver.resolver(tagResolvers);
        }

        public static class LegacySectionTagPreProcessor implements UnaryOperator<String> {
            @Override
            public String apply(String s) {
                return s.replace('§', '&');
            }
        }
    }

    private static class PerColorTagResolver implements TagResolver, SerializableResolver.Single {

        private static final String COLOR_3 = "c";
        private static final String COLOR_2 = "colour";
        private static final String COLOR = "color";

        private static final StyleClaim<TextColor> STYLE = StyleClaim.claim(COLOR, Style::color, (color, emitter) -> {
            if (color instanceof NamedTextColor) {
                emitter.tag(NamedTextColor.NAMES.key((NamedTextColor) color));
            }
        });

        private static final Map<String, TextColor> COLOR_ALIASES = new HashMap<>();

        static {
            COLOR_ALIASES.put("dark_grey", NamedTextColor.DARK_GRAY);
            COLOR_ALIASES.put("grey", NamedTextColor.GRAY);
        }

        private static boolean isColorOrAbbreviation(final String name) {
            return name.equals(COLOR) || name.equals(COLOR_2) || name.equals(COLOR_3);
        }

        private final Set<TextColor> allowedColors = new HashSet<>();

        public PerColorTagResolver(Collection<NamedTextColor> allowedColors) {
            this.allowedColors.addAll(allowedColors);
        }

        @Override
        public @Nullable Tag resolve(final @NotNull String name, final @NotNull ArgumentQueue args, final @NotNull Context ctx) throws ParsingException {
            if (!this.has(name)) {
                return null;
            }

            String colorName;
            if (isColorOrAbbreviation(name)) {
                colorName = args.popOr("Expected to find a color parameter: <name>|#RRGGBB").lowerValue();
            }
            else {
                colorName = name;
            }

            TextColor color = resolveColor(colorName, ctx);

            if (!this.allowedColors.contains(color)) {
                throw ctx.newException(String.format("Color '%s' is not allowed.", colorName));
            }

            return Tag.styling(color);
        }

        static @NotNull TextColor resolveColor(final @NotNull String colorName, final @NotNull Context ctx) throws ParsingException {
            TextColor textColor = COLOR_ALIASES.get(colorName);

            if (textColor != null) {
                return textColor;
            }

            textColor = NamedTextColor.NAMES.value(colorName);

            if (textColor != null) {
                return textColor;
            }

            throw ctx.newException(String.format("Unable to parse a color from '%s'. Please use named colours or hex (#RRGGBB) colors.", colorName));
        }

        @Override
        public boolean has(final @NotNull String name) {
            if (isColorOrAbbreviation(name)) {
                return true;
            }

            NamedTextColor textColor = NamedTextColor.NAMES.value(name);

            if (textColor != null && this.allowedColors.contains(textColor)) {
                return true;
            }

            return COLOR_ALIASES.containsKey(name);
        }

        @Override
        public @Nullable StyleClaim<?> claimStyle() {
            return STYLE;
        }

        public static PerColorTagResolver of(NamedTextColor... allowedColors) {
            return new PerColorTagResolver(Arrays.asList(allowedColors));
        }
    }

    private static class LazyPammHolder {
        private static final PermissionAwareMiniMessage INSTANCE = new PermissionAwareMiniMessage();
    }

    private static class LazyLegacyPammHolder {
        private static final PermissionAwareLegacyMiniMessage INSTANCE = new PermissionAwareLegacyMiniMessage();
    }
}
