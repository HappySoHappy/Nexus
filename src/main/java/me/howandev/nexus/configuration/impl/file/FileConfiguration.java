package me.howandev.nexus.configuration.impl.file;

import me.howandev.nexus.configuration.Configuration;
import me.howandev.nexus.configuration.impl.MemoryConfiguration;
import me.howandev.nexus.configuration.impl.file.json.JsonConfiguration;
import me.howandev.nexus.configuration.impl.file.yaml.YamlConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public abstract class FileConfiguration extends MemoryConfiguration {
    protected final @NotNull File file;
    public FileConfiguration(final @NotNull File file) {
        this.file = file;
    }

    @Contract("-> this")
    public @NotNull FileConfiguration load() throws IOException, IllegalArgumentException {
        StringBuilder builder = new StringBuilder();
        try (BufferedReader input = Files.newBufferedReader(file.toPath())) {
            String currentLine;
            while ((currentLine = input.readLine()) != null) {
                builder.append(currentLine).append('\n');
            }
        }

        return loadFromString(builder.toString());
    }

    @Contract("_ -> this")
    public abstract @NotNull FileConfiguration loadFromString(final @NotNull String contents);

    public void save(final @NotNull File file) throws IOException {
        file.mkdirs();
        file.createNewFile();

        String data = dumpToString();
        try (FileWriter writer = new FileWriter(file, StandardCharsets.UTF_8)) {
            writer.write(data);
        }
    }

    public void save() throws IOException {
        save(file);
    }

    // same as saveToString() in bukkit, but bukkit has horrible naming
    // saveToString would imply its saved, i much more prefer dumpToString(),
    // as that makes it obvious you still need to do something with it.
    public abstract String dumpToString();

    protected void convertMapsToSections(final @NotNull Map<?, ?> input, final @NotNull Configuration section) {
        for (Map.Entry<?, ?> entry : input.entrySet()) {
            String key = entry.getKey().toString();
            Object value = entry.getValue();

            if (value instanceof Map<?, ?> map) {
                convertMapsToSections(map, section.createSection(key));
                continue;
            }

            section.set(key, value);
        }
    }

    public static FileConfiguration fromFile(final @NotNull File file) {
        String fileName = file.getName();
        if (fileName.endsWith(".yaml") || fileName.endsWith(".yml")) {
            return new YamlConfiguration(file);
        }

        if (fileName.endsWith(".json")) {
            return new JsonConfiguration(file);
        }

        return null;
    }
}
