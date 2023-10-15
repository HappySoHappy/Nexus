package me.howandev.nexus;

import java.util.regex.Pattern;

public class NexusConstants {
    public static final Pattern LOCALE_PATTERN = Pattern.compile("^(?<language>[a-zA-Z]+)(?:_(?<country>[A-Za-z]+))?(?:-(?<variant>[A-Za-z]+))?$");

    private NexusConstants() {
        throw new IllegalStateException("Constants class should not be instantiated!");
    }
}
