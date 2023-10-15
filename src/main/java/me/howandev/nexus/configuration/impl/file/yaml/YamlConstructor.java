package me.howandev.nexus.configuration.impl.file.yaml;

import me.howandev.nexus.configuration.Serializer;
import me.howandev.nexus.configuration.serializer.SerializerRegistry;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.constructor.SafeConstructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.Tag;

import java.util.LinkedHashMap;
import java.util.Map;

public class YamlConstructor extends SafeConstructor {
    public YamlConstructor(LoaderOptions loadingConfig) {
        super(loadingConfig);
        yamlConstructors.put(Tag.MAP, new ConstructCustomSerializedMap());
    }

    private class ConstructCustomSerializedMap extends ConstructYamlMap {
        @Override
        public Object construct(Node node) {
            Map<?, ?> raw = (Map<?, ?>) super.construct(node);
            Map<String, Object> constructed = new LinkedHashMap<>();

            for (Map.Entry<?, ?> entry : raw.entrySet()) {
                //TODO: Test behaviour of this hot fix
                //Prevents NullPointerException for empty sections:
                // my_section:
                // #No Values here
                if (entry.getValue() == null) continue;

                Serializer<?> serializer = SerializerRegistry.serializerFor(entry.getValue().getClass());
                if (serializer == null) continue;

                //System.out.printf("Using '%s' for '%s' (%s)%n", serializer.getClass().getSimpleName(), entry.getValue().getClass().getName(), entry.getValue());
                Object value = serializer.deserialize(entry.getValue());
                constructed.put(entry.getKey().toString(), value);
            }

            return constructed;
        }
    }
}
