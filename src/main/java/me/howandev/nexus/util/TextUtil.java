package me.howandev.nexus.util;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    private TextUtil() {
        throw new IllegalStateException("TextUtil class should not be instantiated!");
    }

    public Map<String, Component> getPlaceholderReplacementsFor(Player player) {
        return null;
    }

    public static String replace(final String string, final Pattern pattern, final String replacement) {
        return pattern.matcher(string).replaceAll(Matcher.quoteReplacement(replacement));
    }

    public static String replace(final String string, final String... replacements) throws IllegalArgumentException {
        if (replacements.length < 2)
            throw new IllegalArgumentException("Invalid replacements provided: you must provide minimum 2 replacements in a key-value fashion");

        if (replacements.length % 2 != 0)
            throw new IllegalArgumentException("Invalid replacements provided: you must provide even amount of replacements");

        String message = string;
        for (int i = 0; i < replacements.length; i += 2) {
            String key = replacements[i];
            String value = replacements[i + 1];
            if (value == null)
                value = "";
            message = replace(string, Pattern.compile(Pattern.quote(key)), value);
        }

        return message;
    }
}
