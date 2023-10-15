package me.howandev.nexus.configuration.impl.file.json;

import com.google.gson.*;
import me.howandev.nexus.configuration.Serializer;
import me.howandev.nexus.configuration.serializer.SerializerRegistry;

import java.lang.reflect.Type;

//Object to json
public class JsonSerializer implements com.google.gson.JsonSerializer<Object> {
    private static final Gson gson = new GsonBuilder()
            .serializeNulls()
            .create();

    @Override
    public JsonElement serialize(Object object, Type type, JsonSerializationContext jsonSerializationContext) {
        Serializer<?> serializer = SerializerRegistry.serializerFor(object);
        if (serializer != null) {
            return gson.toJsonTree(serializer.serialize(object));
        }

        return null;
    }
}
