package me.howandev.nexus.configuration.impl.file.yaml;

import me.howandev.nexus.configuration.impl.file.FileConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.representer.Representer;

import java.io.File;
import java.util.Map;

public class YamlConfiguration extends FileConfiguration {
    private static final SafeConstructor CONSTRUCTOR = new YamlConstructor(new LoaderOptions());
    private static final DumperOptions DUMPER_OPTIONS = new DumperOptions();
    private static final Representer REPRESENTER = new YamlRepresenter(DUMPER_OPTIONS);
    private static final Yaml YAML;
    static {
        DUMPER_OPTIONS.setIndent(2); //Let's be real, 2 is much more comfy than 4
        DUMPER_OPTIONS.setPrettyFlow(true);
        YAML = new Yaml(CONSTRUCTOR, REPRESENTER, DUMPER_OPTIONS);
    }

    //TODO: java docs
    /**
     * Creates initially empty configuration with specified file to load from.
     * Before you can retrieve any values you must call load(), or load(File)
     *
     * @param file file to load from
     */
    public YamlConfiguration(final @NotNull File file) {
        super(file);
    }

    @Override
    @Contract("_ -> this")
    public @NotNull FileConfiguration loadFromString(final @NotNull String contents) throws IllegalArgumentException {
        Map<?, ?> loadedStore;
        try {
            loadedStore = YAML.load(contents);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }

        store.clear();
        if (loadedStore != null)
            convertMapsToSections(loadedStore, this);

        return this;
    }

    @Override
    public String dumpToString() {
        return YAML.dumpAsMap(getValues(false)); //Explicit FlowStyle = BLOCK
    }
}
