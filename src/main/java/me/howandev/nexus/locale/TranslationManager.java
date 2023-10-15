package me.howandev.nexus.locale;

import me.howandev.nexus.NexusConstants;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TranslationManager {
    public static final Locale DEFAULT_LOCALE = ExtraLocale.POLISH;
    public static final Set<Locale> SUPPORTED_LOCALES = Set.of(DEFAULT_LOCALE, ExtraLocale.ENGLISH);

    private TranslationRegistry registry; // adventure
    private final JavaPlugin plugin;
    private final Set<Locale> installedLocales;
    private final Path translationsDirectory;
    public TranslationManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.translationsDirectory = this.plugin.getDataFolder().toPath().resolve("locales");
        this.installedLocales = new HashSet<>();

        try {
            Files.createDirectories(translationsDirectory);
        } catch (IOException ignored) { }
    }

    public void reload() {
        if (this.registry != null) {
            GlobalTranslator.translator().removeSource(this.registry);
            this.installedLocales.clear();
        }

        // create a translation registry
        this.registry = TranslationRegistry.create(Key.key("nexus", "translations"));
        this.registry.defaultLocale(DEFAULT_LOCALE);

        loadFromFileSystem(translationsDirectory, false);
        loadFromResourceBundle();


        // register it to the global source, so our translations can be picked up by adventure-platform
        GlobalTranslator.translator().addSource(this.registry);
    }

    private Map.Entry<Locale, ResourceBundle> loadTranslationBundle(Path translationFile) throws IOException {
        String fileName = translationFile.getFileName().toString();
        String localeString = fileName.substring(0, fileName.length() - ".properties".length());
        Locale locale = parseLocale(localeString);

        PropertyResourceBundle bundle;
        try (BufferedReader reader = Files.newBufferedReader(translationFile, StandardCharsets.UTF_8)) {
            bundle = new PropertyResourceBundle(reader);
        }

        this.registry.registerAll(locale, bundle, false);
        this.installedLocales.add(locale);
        return Map.entry(locale, bundle);
    }

    public static boolean isTranslationFile(Path path) {
        return path.getFileName().toString().endsWith(".properties");
    }

    public void loadFromFileSystem(Path directory, boolean suppressDuplicatesError) {
        List<Path> translationFiles;
        try (Stream<Path> stream = Files.list(directory)) {
            translationFiles = stream.filter(TranslationManager::isTranslationFile).collect(Collectors.toList());
        } catch (IOException e) {
            translationFiles = Collections.emptyList();
        }

        if (translationFiles.isEmpty()) {
            return;
        }

        Map<Locale, ResourceBundle> loaded = new HashMap<>();
        for (Path translationFile : translationFiles) {
            try {
                Map.Entry<Locale, ResourceBundle> result = loadTranslationBundle(translationFile);
                loaded.put(result.getKey(), result.getValue());
            } catch (Exception e) {
                if (!suppressDuplicatesError || !isAdventureDuplicatesException(e)) {
                    this.plugin.getLogger().warning("Error loading locale file: " + translationFile.getFileName());
                    e.printStackTrace();
                }
            }
        }

        // try registering the locale without a country code - if we don't already have a registration for that
        loaded.forEach((locale, bundle) -> {
            Locale localeWithoutCountry = new Locale(locale.getLanguage());
            if (!locale.equals(localeWithoutCountry) /*&& !localeWithoutCountry.equals(DEFAULT_LOCALE)*/ && this.installedLocales.add(localeWithoutCountry)) {
                try {
                    this.registry.registerAll(localeWithoutCountry, bundle, false);
                } catch (IllegalArgumentException e) {
                    // ignore
                }
            }
        });
    }

    private void loadFromResourceBundle() {
        for (Locale locale : SUPPORTED_LOCALES) {
            ResourceBundle bundle = ResourceBundle.getBundle("nexus", locale, getClass().getClassLoader());
            try {
                this.registry.registerAll(locale, bundle, false);
            } catch (IllegalArgumentException e) {
                if (!isAdventureDuplicatesException(e)) {
                    this.plugin.getLogger().warning("Error loading '" + locale + "' locale file");
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean isAdventureDuplicatesException(Exception e) {
        return e instanceof IllegalArgumentException && (e.getMessage().startsWith("Invalid key") || e.getMessage().startsWith("Translation already exists"));
    }

    // Language_Country-Variant
    private Locale parseLocale(String localeString) {
        Matcher matcher = NexusConstants.LOCALE_PATTERN.matcher(localeString);
        if (!matcher.matches())
            throw new IllegalArgumentException(String.format("Provided localeString '%s' is not a valid ISO-639 locale!", localeString));

        String language = matcher.group("language");
        String country = matcher.group("country");
        String variant = matcher.group("variant");

        if (country != null && variant != null)
            return new Locale(language, country, variant);

        if (country != null)
            return new Locale(language, country);

        return new Locale(language);
    }
}
