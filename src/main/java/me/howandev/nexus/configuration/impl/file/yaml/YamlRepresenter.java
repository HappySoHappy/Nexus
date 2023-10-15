package me.howandev.nexus.configuration.impl.file.yaml;

import me.howandev.nexus.configuration.Serializer;
import me.howandev.nexus.configuration.serializer.SerializerRegistry;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Representer;

public class YamlRepresenter extends Representer {
    public YamlRepresenter(DumperOptions options) {
        super(options);
        multiRepresenters.put(Object.class, new RepresentCustomSerializedMap());
    }

    private class RepresentCustomSerializedMap extends RepresentMap {
        @Override
        public Node representData(Object data) {
            Serializer<?> serializer = SerializerRegistry.serializerFor(data.getClass());
            if (serializer != null) {
                return super.representData(serializer.serialize(data));
            }

            return super.representData(data);
        }
    }
}
