package me.howandev.nexus.configuration;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public interface Configuration {
    String PATH_SEPARATOR = ".";
    Pattern SEPARATOR_PATTERN = Pattern.compile(Pattern.quote(PATH_SEPARATOR));

    @Nullable Configuration getParent();

    @NotNull Configuration getRoot();

    @NotNull String createPath(final @NotNull Configuration section, final @Nullable String path);

    @NotNull String getPath();

    @NotNull String getFullPath();

    @NotNull Configuration createSection(final @NotNull String path);

    boolean isSection(final @NotNull String path);

    @Nullable Configuration getSection(final @NotNull String path);

    @NotNull Set<String> getKeys(final boolean deep);

    @NotNull Map<String, Object> getValues(final boolean deep);

    /**
     * Checks if this {@link Configuration} has a value set for the
     * given path.
     *
     * <p>If the value for the requested path does not exist but a default value
     * has been specified, this will still return false.
     */
    boolean isSet(final @NotNull String path);

    void set(final @NotNull String path, final @Nullable Object value);

    @Nullable Object get(final @NotNull String path);

    @Contract("_, !null -> !null")
    @Nullable Object get(final @NotNull String path, final @Nullable Object defaultValue);

    boolean isBoolean(final @NotNull String path);

    @Contract("_, !null -> !null")
    @Nullable Boolean getBoolean(final @NotNull String path, final @Nullable Boolean defaultValue);

    @Nullable Boolean getBoolean(final @NotNull String path);

    boolean isByte(final @NotNull String path);

    @Contract("_, !null -> !null")
    @Nullable Byte getByte(final @NotNull String path, final @Nullable Byte defaultValue);

    @Nullable Byte getByte(final @NotNull String path);

    boolean isCharacter(final @NotNull String path);

    @Contract("_, !null -> !null")
    @Nullable Character getCharacter(final @NotNull String path, final @Nullable Character defaultValue);

    @Nullable Character getCharacter(final @NotNull String path);

    boolean isShort(final @NotNull String path);

    @Contract("_, !null -> !null")
    @Nullable Short getShort(final @NotNull String path, final @Nullable Short defaultValue);

    @Nullable Short getShort(final @NotNull String path);

    boolean isInteger(final @NotNull String path);

    @Contract("_, !null -> !null")
    @Nullable Integer getInteger(final @NotNull String path, final @Nullable Integer defaultValue);

    @Nullable Integer getInteger(final @NotNull String path);

    boolean isFloat(final @NotNull String path);

    @Contract("_, !null -> !null")
    @Nullable Float getFloat(final @NotNull String path, final @Nullable Float defaultValue);

    @Nullable Float getFloat(final @NotNull String path);

    boolean isLong(final @NotNull String path);

    @Contract("_, !null -> !null")
    @Nullable Long getLong(final @NotNull String path, final @Nullable Long defaultValue);

    @Nullable Long getLong(final @NotNull String path);

    boolean isDouble(final @NotNull String path);

    @Contract("_, !null -> !null")
    @Nullable Double getDouble(final @NotNull String path, final @Nullable Double defaultValue);

    @Nullable Double getDouble(final @NotNull String path);

    boolean isString(final @NotNull String path);

    @Contract("_, !null -> !null")
    @Nullable String getString(final @NotNull String path, final @Nullable String defaultValue);

    @Nullable String getString(final @NotNull String path);

    boolean isList(final @NotNull String path);

    @Contract("_, !null -> !null")
    @Nullable List<?> getList(final @NotNull String path, final @Nullable List<?> defaultValue);

    @Nullable List<?> getList(final @NotNull String path);

    boolean isMap(final @NotNull String path);

    @Contract("_, !null -> !null")
    @Nullable Map<?, ?> getMap(final String path, final Map<?, ?> defaultValue);

    @Nullable Map<?, ?> getMap(final String path);

    <E extends Enum<E>> boolean isEnumConstant(final @NotNull Class<E> enumClass, final @NotNull String path);

    @Contract("_, _, !null -> !null")
    @Nullable <E extends Enum<E>> E getEnumConstant(final @NotNull Class<E> enumClass, final @NotNull String path, final @Nullable E defaultValue);

    @Nullable <E extends Enum<E>> E getEnumConstant(final @NotNull Class<E> enumClass, final @NotNull String path);
}
