package me.howandev.nexus.configuration.impl;

import me.howandev.nexus.configuration.Configuration;
import me.howandev.nexus.configuration.serializer.SerializerRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;

public class MemoryConfiguration implements Configuration {
    private final @Nullable Configuration parent;
    private final @NotNull Configuration root;
    private final @NotNull String path;
    private final @NotNull String fullPath;
    protected final Map<String, Object> store = new LinkedHashMap<>();
    public MemoryConfiguration() {
        this.parent = null;
        this.root = this;
        this.path = "";
        this.fullPath = "";
    }

    public MemoryConfiguration(final @NotNull Configuration parent) {
        this.parent = parent;
        this.root = parent.getRoot();
        this.path = "";
        this.fullPath = createPath(parent, root, path);
    }

    public MemoryConfiguration(final @NotNull Configuration parent, final @Nullable String path) {
        this.parent = parent;
        this.root = parent.getRoot();
        this.path = (path != null) ? path : "";
        this.fullPath = createPath(parent, root, path);
    }

    @Override
    public @Nullable Configuration getParent() {
        return this.parent;
    }

    @Override
    public @NotNull Configuration getRoot() {
        return this.root;
    }

    @Override
    public @NotNull String getPath() {
        return this.path;
    }

    @Override
    public @NotNull String getFullPath() {
        return this.fullPath;
    }

    public static @NotNull String createPath(final @NotNull Configuration section, final @Nullable Configuration relativeTo, final @Nullable String path) {
        StringBuilder builder = new StringBuilder();
        for (Configuration currentParent = section; (currentParent != null) && (currentParent != relativeTo); currentParent = currentParent.getParent()) {
            if (builder.length() > 0)
                builder.insert(0, PATH_SEPARATOR);

            builder.insert(0, currentParent.getPath());
        }

        if ((path != null) && !path.isEmpty()) {
            if (builder.length() > 0)
                builder.append(PATH_SEPARATOR);

            builder.append(path);
        }

        return builder.toString();
    }

    @Override
    public @NotNull String createPath(final @NotNull Configuration section, final @Nullable String path) {
        return createPath(section, this, path);
    }

    @Override
    public @NotNull Configuration createSection(final @NotNull String path) {
        Configuration section = this;

        int leading = -1; //i1 leading (higher) index
        int trailing; //i2 trailing (lower) index
        Matcher matcher = SEPARATOR_PATTERN.matcher(path);
        while (matcher.find(trailing = leading + 1)) {
            leading = matcher.start();
            String node = path.substring(trailing, leading);
            Configuration subSection = section.getSection(node);
            if (subSection == null) {
                section = section.createSection(node);
                continue;
            }

            section = subSection;
        }

        String key = path.substring(trailing);
        if (section == this) {
            Configuration result = new MemoryConfiguration(this, key);
            store.put(key, result);
            return result;
        }

        return section.createSection(key);
    }

    @Override
    public boolean isSection(final @NotNull String path) {
        return get(path) instanceof Configuration;
    }

    @Override
    public @Nullable Configuration getSection(final @NotNull String path) {
        return (get(path) instanceof Configuration configurationValue) ? configurationValue : null;
    }

    //todo: merge with getKeys()
    protected void mapChildrenKeys(final @NotNull Set<String> output, final @NotNull Configuration configuration, final boolean deep) {
        if (configuration instanceof MemoryConfiguration memoryConfiguration) {
            memoryConfiguration.store.forEach((key, value) -> {
                output.add(createPath(configuration, this, key));

                if (deep && value instanceof Configuration subConfiguration) {
                    mapChildrenKeys(output, subConfiguration, deep);
                }
            });
            return;
        }

        for (String key : configuration.getKeys(deep)) {
            output.add(createPath(configuration, this, key));
        }
    }

    //todo: merge with getValues()
    protected void mapChildrenValues(final @NotNull Map<String, Object> output, final @NotNull Configuration configuration, final boolean deep) {
        if (configuration instanceof MemoryConfiguration memoryConfiguration) { //technically always true
            memoryConfiguration.store.forEach((key, value) -> {
                output.put(createPath(configuration, this, key), value);

                if (deep && value instanceof Configuration valueConfiguration) {
                    mapChildrenValues(output, valueConfiguration, deep);
                }
            });
            return;
        }

        for (Map.Entry<String, Object> entry : configuration.getValues(deep).entrySet()) {
            output.put(createPath(configuration, this, entry.getKey()), entry.getValue());
        }
    }

    @Override
    public @NotNull Set<String> getKeys(final boolean deep) {
        Set<String> result = new LinkedHashSet<>();
        mapChildrenKeys(result, this, deep);

        return result;
    }

    @Override
    public @NotNull Map<String, Object> getValues(final boolean deep) {
        Map<String, Object> result = new LinkedHashMap<>();
        mapChildrenValues(result, this, deep);

        return result;
    }

    @Override
    public boolean isSet(final @NotNull String path) {
        return get(path) != null;
    }

    @Override
    public void set(final @NotNull String path, final @Nullable Object value) {
        Configuration section = this;

        int leading = -1; //i1 leading (higher) index
        int trailing; //i2 trailing (lower) index

        Matcher matcher = SEPARATOR_PATTERN.matcher(path);
        while (matcher.find(trailing = leading + 1)) {
            leading = matcher.start();
            String node = path.substring(trailing, leading);
            Configuration subSection = section.getSection(node);
            if (subSection == null) {
                section = section.createSection(node);
                continue;
            }

            section = subSection;
        }

        String key = path.substring(trailing);
        if (section == this) {
            if (value == null)
                store.remove(key);

            store.put(key, value);
            return;
        }

        section.set(key, value);
    }

    @Override
    public @Nullable Object get(final @NotNull String path) {
        return get(path, null);
    }

    @Override
    public @Nullable Object get(final @NotNull String path, final @Nullable Object defaultValue) {
        if (path.isEmpty())
            return this;

        Configuration section = this;

        int leading = -1; //i1 leading (higher) index
        int trailing; //i2 trailing (lower) index

        Matcher matcher = SEPARATOR_PATTERN.matcher(path);
        while (matcher.find(trailing = leading + 1)) {
            leading = matcher.start();
            section = section.getSection(path.substring(trailing, leading));
            if (section == null)
                return defaultValue;
        }

        String key = path.substring(trailing);
        if (section == this) {
            Object result = store.get(key);
            if (result == null)
                return defaultValue;

            return result;
        }

        return section.get(key, defaultValue);
    }

    @Override
    public boolean isBoolean(final @NotNull String path) {
        return (get(path) instanceof Boolean);
    }

    @Override
    public @Nullable Boolean getBoolean(final @NotNull String path, final @Nullable Boolean defaultValue) {
        return SerializerRegistry.BOOLEAN_SERIALIZER.parse(get(path), defaultValue);
    }

    @Override
    public @Nullable Boolean getBoolean(final @NotNull String path) {
        return getBoolean(path, null);
    }

    @Override
    public boolean isByte(final @NotNull String path) {
        return (get(path) instanceof Byte);
    }

    @Override
    public @Nullable Byte getByte(final @NotNull String path, final @Nullable Byte defaultValue) {
        return SerializerRegistry.BYTE_SERIALIZER.parse(get(path), defaultValue);
    }

    @Override
    public @Nullable Byte getByte(final @NotNull String path) {
        return getByte(path, null);
    }

    @Override
    public boolean isCharacter(final @NotNull String path) {
        return (get(path) instanceof Character);
    }

    @Override
    public @Nullable Character getCharacter(final @NotNull String path, final @Nullable Character defaultValue) {
        return SerializerRegistry.CHARACTER_SERIALIZER.parse(get(path), defaultValue);
    }

    @Override
    public @Nullable Character getCharacter(final @NotNull String path) {
        return getCharacter(path, null);
    }

    @Override
    public boolean isShort(final @NotNull String path) {
        return (get(path) instanceof Short);
    }

    @Override
    public @Nullable Short getShort(final @NotNull String path, final @Nullable Short defaultValue) {
        return SerializerRegistry.SHORT_SERIALIZER.parse(get(path), defaultValue);
    }

    @Override
    public @Nullable Short getShort(final @NotNull String path) {
        return getShort(path, null);
    }

    @Override
    public boolean isInteger(final @NotNull String path) {
        return (get(path) instanceof Integer);
    }

    @Override
    public @Nullable Integer getInteger(final @NotNull String path, final @Nullable Integer defaultValue) {
        return SerializerRegistry.INTEGER_SERIALIZER.parse(get(path), defaultValue);
    }

    @Override
    public @Nullable Integer getInteger(final @NotNull String path) {
        return getInteger(path, null);
    }

    @Override
    public boolean isFloat(final @NotNull String path) {
        return (get(path) instanceof Float);
    }

    @Override
    public @Nullable Float getFloat(final @NotNull String path, final @Nullable Float defaultValue) {
        return SerializerRegistry.FLOAT_SERIALIZER.parse(get(path), defaultValue);
    }

    @Override
    public @Nullable Float getFloat(final @NotNull String path) {
        return getFloat(path, null);
    }

    @Override
    public boolean isLong(final @NotNull String path) {
        return (get(path) instanceof Long);
    }

    @Override
    public @Nullable Long getLong(final @NotNull String path, final @Nullable Long defaultValue) {
        return SerializerRegistry.LONG_SERIALIZER.parse(get(path), defaultValue);
    }

    @Override
    public @Nullable Long getLong(final @NotNull String path) {
        return getLong(path, null);
    }

    @Override
    public boolean isDouble(final @NotNull String path) {
        return (get(path) instanceof Double);
    }

    @Override
    public @Nullable Double getDouble(final @NotNull String path, final @Nullable Double defaultValue) {
        return SerializerRegistry.DOUBLE_SERIALIZER.parse(get(path), defaultValue);
    }

    @Override
    public @Nullable Double getDouble(final @NotNull String path) {
        return getDouble(path, null);
    }

    @Override
    public boolean isString(final @NotNull String path) {
        return (get(path) instanceof String);
    }

    @Override
    public @Nullable String getString(final @NotNull String path, final @Nullable String defaultValue) {
        Object value = get(path);
        if (value == null) return defaultValue;

        return String.valueOf(value);
    }

    @Override
    public @Nullable String getString(final @NotNull String path) {
        return getString(path, null);
    }

    @Override
    public boolean isList(final @NotNull String path) {
        return (get(path) instanceof List<?>);
    }

    @Override
    public @Nullable List<?> getList(final @NotNull String path, final @Nullable List<?> defaultValue) {
        return SerializerRegistry.LIST_SERIALIZER.parse(get(path), defaultValue);
    }

    @Override
    public @Nullable List<?> getList(final @NotNull String path) {
        return getList(path, null);
    }

    @Override
    public boolean isMap(final @NotNull String path) {
        return (get(path) instanceof Map<?,?>) || (isSection(path));
    }

    @Override
    public @Nullable Map<?, ?> getMap(final @NotNull String path, final @Nullable Map<?, ?> defaultValue) {
        if (isSection(path))
            return SerializerRegistry.MAP_SERIALIZER.parse(getSection(path), defaultValue);

        return SerializerRegistry.MAP_SERIALIZER.parse(get(path), defaultValue);
    }

    public @Nullable Map<?, ?> getMap(final @NotNull String path) {
        return getMap(path, null);
    }

    @Override
    public <E extends Enum<E>> boolean isEnumConstant(final @NotNull Class<E> enumClass, final @NotNull String path) {
        try {
            E.valueOf(enumClass, String.valueOf(get(path)).toUpperCase(Locale.ROOT));
            return true;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    @Override
    public <E extends Enum<E>> @Nullable E getEnumConstant(final @NotNull Class<E> enumClass, final @NotNull String path, final @Nullable E defaultValue) {
        try {
            return E.valueOf(enumClass, String.valueOf(get(path)).toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            return defaultValue;
        }
    }

    @Override
    public <E extends Enum<E>> @Nullable E getEnumConstant(final @NotNull Class<E> enumClass, final @NotNull String path) {
        return getEnumConstant(enumClass, path, null);
    }
}
