package me.howandev.nexus.i18n;

import me.howandev.nexus.configuration.Configuration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * The {@link TranslationRegistry} class provides a mechanism for managing and retrieving translations for a given context,
 * which must be able to provide a {@link Locale} for the {@link TranslationRegistry}.
 * <p>Translation messages are mapped using {@link TranslationKey} as the key and the translated text as the value.
 * <p>{@link TranslationRegistry} can be used to set individual translation messages, set multiple messages at once, or add a ResourceBundle
 *
 * @apiNote The {@link TranslationRegistry} class is designed to be thread-safe,
 * and is backed by a {@link ConcurrentHashMap} to store the translated messages,
 * it's also backed by a {@link AtomicReference} {@link Locale} for default locale argument.
 */
public class TranslationRegistry<T> {
    private final Map<Locale, TranslationTable> translationStore = new ConcurrentHashMap<>();
    private final Function<T, Locale> localeMapper;
    private final @NotNull AtomicReference<Locale> defaultLocale;
    private TranslationRegistry(final Function<T, Locale> localeMapper, final @NotNull Locale defaultLocale) {
        this.localeMapper = localeMapper;
        this.defaultLocale = new AtomicReference<>(defaultLocale);
    }

    public static <T> TranslationRegistry<T> create(final @NotNull Function<T, Locale> localeMapper, final Locale defaultLocale) {
        return new TranslationRegistry<>(localeMapper, defaultLocale);
    }

    public static <T> TranslationRegistry<T> create(final @NotNull Function<T, Locale> localeMapper) {
        return create(localeMapper, Locale.ENGLISH);
    }

    public @NotNull TranslationTable getTranslationTable(final @NotNull Locale locale) {
        return translationStore.computeIfAbsent(locale, TranslationTable::new);
    }

    public @NotNull Set<Locale> getLoadedLocales() {
        return Collections.unmodifiableSet(translationStore.keySet());
    }

    public @NotNull Locale getDefaultLocale() {
        return defaultLocale.get();
    }

    //TODO: in future, add per-context locale support
    public @NotNull Locale getLocale(final @NotNull T context) {
        return localeMapper.apply(context);
    }

    @Contract("_ -> this")
    public TranslationRegistry<T> setDefaultLocale(final @NotNull Locale defaultLocale) {
        this.defaultLocale.set(defaultLocale);
        return this;
    }

    @Contract("_, _, _ -> this")
    public TranslationRegistry<T> setMessage(final @NotNull Locale locale, final @NotNull TranslationKey key, final @NotNull String message) {
        getTranslationTable(locale).setMessage(key, message);
        return this;
    }

    @Contract("_, _ -> this")
    public TranslationRegistry<T> setMessages(final @NotNull Locale locale, final @NotNull Map<TranslationKey, String> messages) {
        getTranslationTable(locale).setMessages(messages);
        return this;
    }

    @Contract("_, _, _ -> this")
    public TranslationRegistry<T> addMessageBundle(final @NotNull ClassLoader classLoader, final @NotNull String bundleName, final @NotNull Locale... locales) {
        for (Locale locale : locales) {
            getTranslationTable(locale).addMessageBundle(classLoader, bundleName);
        }
        return this;
    }

    @Contract("_, _ -> this")
    public TranslationRegistry<T> addMessageBundle(final @NotNull String bundleName, final @NotNull Locale... locales) {
        addMessageBundle(getClass().getClassLoader(), bundleName, locales);
        return this;
    }

    @Contract("_, _ -> this")
    public TranslationRegistry<T> addMessageConfiguration(final @NotNull Configuration config, final @NotNull Locale locale) {
        for (String key : config.getKeys(true)) {
            if (!config.isString(key) && !config.isDouble(key) && !config.isLong(key) && !config.isInteger(key) && !config.isBoolean(key))
                continue;

            String value = config.getString(key);
            if (value != null && !value.isEmpty())
                setMessage(locale, TranslationKey.of(key), value);
        }
        return this;
    }

    /**
     * Returns the text that is associated with specified {@link TranslationKey}.
     * Messages will be retrieved in following order:
     * <p>1. Message from context-provided {@link Locale} (includes country-specific dialects),
     * <p>2. Message from context-provided {@link Locale#getLanguage()} (ignore country-specific dialects),
     * <p>3. Message from {@link TranslationRegistry#getDefaultLocale()} (if defaultLocaleFallback is {@code true})
     * <p>4. {@code null} if no text is associated with the specified {@link TranslationKey}.
     *
     * @param key the {@link TranslationKey}.
     * @param defaultLocaleFallback if true, and if there is no message associated with context-provided {@link Locale},
     * {@link TranslationRegistry#getDefaultLocale()} will be used instead.
     * @return The localized text or {@code null} if no text is associated with the specified {@link TranslationKey}.
     */
    public @Nullable String getMessage(final @NotNull T context, final @NotNull TranslationKey key, final boolean defaultLocaleFallback) {
        Locale locale = getLocale(context);
        String message = getTranslationTable(locale).getMessage(key);
        if (message == null && !locale.getLanguage().isEmpty())
            message = getTranslationTable(new Locale(locale.getLanguage())).getMessage(key);

        if (message == null && defaultLocaleFallback)
            message = getTranslationTable(getDefaultLocale()).getMessage(key);

        return message;
    }

    /**
     * Returns the text that is associated with specified {@link TranslationKey}.
     * Messages will be retrieved in following order:
     * <p>1. Message from context-provided {@link Locale} (includes country-specific dialects),
     * <p>2. Message from context-provided {@link Locale#getLanguage()} (ignore country-specific dialects),
     * <p>3. Message from defined {@link TranslationRegistry#getDefaultLocale()}
     * <p>4. {@code null} if no text is associated with the specified {@link TranslationKey}.
     *
     * @param key the {@link TranslationKey}.
     * @return The localized text or {@code null} if no text is associated with the specified {@link TranslationKey}.
     */
    public @Nullable String getMessage(final @NotNull T context, final @NotNull TranslationKey key) {
        return getMessage(context, key, true);
    }
}
