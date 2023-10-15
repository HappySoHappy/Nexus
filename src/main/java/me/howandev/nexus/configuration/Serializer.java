package me.howandev.nexus.configuration;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

public interface Serializer<T> {
    String SERIALIZED_OBJECT_KEY = "==";
    Pattern FORCE_SERIALIZER_PATTERN = Pattern.compile("^\\((\\S.*)\\) (.*)");
    @NotNull String getAlias();
    @NotNull Object serialize(final @NotNull Object value) throws IllegalArgumentException;
    //value straight from parser
    @NotNull Object deserialize(final @NotNull Object value) throws IllegalArgumentException;
    boolean canHandle(final @NotNull Object value);
    //value from config store (9/10 already deserialized correctly)
    @Contract("_, !null -> !null")
    @Nullable T parse(final @Nullable Object value, final @Nullable T defaultValue);
    default @Nullable T parse(final @Nullable Object value) {
        return parse(value, null);
    }
}
