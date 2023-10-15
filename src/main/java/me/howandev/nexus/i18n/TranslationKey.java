package me.howandev.nexus.i18n;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link TranslationKey} class represents a unique identifier.<p>
 *
 * @apiNote The {@link TranslationKey} class is designed to be thread-safe,
 * and is backed by a {@link ConcurrentHashMap} to store the values.
 */
public class TranslationKey {
    protected static final Map<String, TranslationKey> KEY_STORE = new ConcurrentHashMap<>();
    private static final AtomicInteger HASH_COUNTER = new AtomicInteger();
    private final int hash = HASH_COUNTER.getAndIncrement();
    private final @NotNull String key;
    protected TranslationKey(final @NotNull String key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return key;
    }

    public static @NotNull TranslationKey of(final @NotNull String key) {
        return KEY_STORE.computeIfAbsent(key.toLowerCase().intern(), TranslationKey::new);
    }
}
