package me.howandev.nexus.i18n;

import lombok.Getter;
import lombok.Setter;
import me.howandev.nexus.NexusConstants;
import me.howandev.nexus.configuration.impl.file.yaml.YamlConfiguration;
import me.howandev.nexus.util.FileUtil;
import me.howandev.nexus.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationManager {
    public static final Pattern TRANSLATE_PATTERN = Pattern.compile("%\\{(?:lang|translate|tr):(?<key>[^:]+)}%", Pattern.CASE_INSENSITIVE);
    private final JavaPlugin plugin;
    @Getter
    private final TranslationRegistry<Player> registry;
    private final Map<UUID, Locale> playerLocaleStore = new HashMap<>();
    @Getter
    @Setter
    private boolean perPlayerLocale = false;
    public TranslationManager(final @NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = TranslationRegistry.create(this::getPlayerLocale);
    }

    public TranslationManager(final @NotNull JavaPlugin plugin, final @NotNull Locale defaultLocale) {
        this.plugin = plugin;
        this.registry = TranslationRegistry.create(this::getPlayerLocale, defaultLocale);
    }

    public void loadTranslations() {
        File localesDirectory = new File(plugin.getDataFolder(), "locales");
        if (!localesDirectory.exists())
            return;

        Set<File> localeFiles = FileUtil.listFileTree(localesDirectory);
        for (File localeFile : localeFiles) {
            try {
                //Bukkit configuration sucks ass, i much more prefer my take on it.
                YamlConfiguration config = new YamlConfiguration(localeFile);
                config.load();

                int dotIndex = localeFile.getName().lastIndexOf('.');
                String localeString = (dotIndex == -1) ? localeFile.getName() : localeFile.getName().substring(0, dotIndex);

                Matcher matcher = NexusConstants.LOCALE_PATTERN.matcher(localeString);
                if (!matcher.matches())
                    throw new IllegalArgumentException(String.format("Provided localeString '%s' is not a valid ISO-639 locale!", localeString));

                String language = matcher.group("language");
                String country = matcher.group("country");
                String variant = matcher.group("variant");
                if (country != null && variant != null) //todo: loads with variant
                    registry.addMessageConfiguration(config, new Locale(language, country, variant));

                if (country != null) //todo: after loading with variant the same locale will be used for country
                    registry.addMessageConfiguration(config, new Locale(language, country));

                registry.addMessageConfiguration(config, new Locale(language)); //todo: after loading with country same locale will be used for language
                plugin.getLogger().info(String.format("Successfully loaded '%s' translation from '%s'", localeString, localeFile.getName()));
            } catch (IOException | IllegalStateException | IllegalArgumentException ex) {
                plugin.getLogger().severe(String.format("Failed to load language file '%s', %s", localeFile.getName(), ex.getMessage()));
            }
        }
    }

    //TODO: get locale by permission aswell...
    public @NotNull Locale getPlayerLocale(final @NotNull Player player) {
        if (!perPlayerLocale)
            return registry.getDefaultLocale();

        //Forced locale
        if (playerLocaleStore.containsKey(player.getUniqueId()))
            return playerLocaleStore.get(player.getUniqueId());

        if (!player.isOnline())
            return registry.getDefaultLocale();

        try {
            Field entityField = getEntityField(player);
            if (entityField == null)
                return registry.getDefaultLocale();

            Object nmsPlayer = entityField.get(player);
            if (nmsPlayer == null)
                return registry.getDefaultLocale();

            Field localeField = nmsPlayer.getClass().getDeclaredField("locale");
            localeField.setAccessible(true);
            Object localeObject = localeField.get(nmsPlayer);
            if (!(localeObject instanceof String localeString))
                return registry.getDefaultLocale();

            Matcher matcher = NexusConstants.LOCALE_PATTERN.matcher(localeString);
            if (matcher.matches()) {
                String language = matcher.group("language");
                String country = matcher.group("country");
                String variant = matcher.group("variant");

                if (country != null && variant != null)
                    return new Locale(language, country, variant);

                if (country != null)
                    return new Locale(language, country);

                return new Locale(language);
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) { }

        return registry.getDefaultLocale();
    }

    public void setPlayerLocale(final @NotNull Player player, final @NotNull Locale locale) {
        playerLocaleStore.put(player.getUniqueId(), locale);
        if (!player.isOnline())
            return;

        try {
            Field entityField = getEntityField(player);
            if (entityField == null)
                return;

            Object nmsPlayer = entityField.get(player);
            if (nmsPlayer == null)
                return;

            Field localeField = nmsPlayer.getClass().getDeclaredField("locale");
            localeField.setAccessible(true);
            localeField.set(nmsPlayer, locale.toString().toLowerCase(Locale.ROOT));
        } catch (NoSuchFieldException | IllegalAccessException ignored) { }
    }

    private static @Nullable Field getEntityField(final @NotNull Player player) throws NoSuchFieldException {
        Class<?> clazz = player.getClass();
        while (clazz != Object.class) {
            if (clazz.getName().endsWith("CraftEntity")) {
                Field field = clazz.getDeclaredField("entity");
                field.setAccessible(true);
                return field;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    private @NotNull String getMessage(final Player player, final TranslationKey key) {
        String message = registry.getMessage(player, key, true);
        if (message != null)
            return message;

        plugin.getLogger().warning(String.format("Missing translation: '%s' for '%s' locale", key, registry.getLocale(player)));
        return String.format("<missing:%s>", key);
    }

    private @NotNull String replaceTranslations(final @NotNull Player player, final @NotNull String message) {
        Matcher matcher = TRANSLATE_PATTERN.matcher(message);
        if (!matcher.find())
            return message;

        matcher.reset();
        StringBuilder sb = new StringBuilder(message.length());
        while (matcher.find()) {
            TranslationKey key = TranslationKey.of(matcher.group("key"));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(getMessage(player, key)));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public @NotNull String getTranslation(final @NotNull Player player, final @NotNull TranslationKey key, final String... replacements) {
        String message = getMessage(player, key);
        if (replacements.length > 0) {
            message = TextUtil.replace(message, replacements);
        }

        message = replaceTranslations(player, message);
        return message;
    }

    public @NotNull String getTranslation(final @NotNull Player player, final @NotNull String key, final String... replacements) {
        String message = getMessage(player, TranslationKey.of(key));
        if (replacements.length > 0) {
            message = TextUtil.replace(message, replacements);
        }

        message = replaceTranslations(player, message);
        return message;
    }
}
