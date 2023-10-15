package me.howandev.nexus.configuration.impl.file.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.howandev.nexus.configuration.impl.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

public class JsonConfiguration extends FileConfiguration {
    private static final JsonDeserializer JSON_DESERIALIZER = new JsonDeserializer();
    private static final JsonSerializer JSON_SERIALIZER = new JsonSerializer();
    private static final Gson GSON;
    static {
        GSON = new GsonBuilder()
                .registerTypeHierarchyAdapter(Object.class, JSON_DESERIALIZER)
                .registerTypeHierarchyAdapter(Object.class, JSON_SERIALIZER)
                .serializeNulls()
                .disableHtmlEscaping()
                .setPrettyPrinting()
                .create();
    }

    public JsonConfiguration(@NotNull File file) {
        super(file);
        System.out.println("WARNING! JsonConfiguration class is experimental and may change at any time without warning!");
    }

    @Override
    public @NotNull FileConfiguration loadFromString(@NotNull String contents) {
        Map<?, ?> loadedStore;
        try {
            loadedStore = GSON.fromJson(contents, Map.class);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }

        if (loadedStore != null) {
            convertMapsToSections(loadedStore, this);
        }

        return this;
    }

    @Override
    public String dumpToString() {
        return GSON.toJson(getValues(false));
    }
}
