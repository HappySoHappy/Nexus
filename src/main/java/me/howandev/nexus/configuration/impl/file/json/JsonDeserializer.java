package me.howandev.nexus.configuration.impl.file.json;

import com.google.gson.*;
import me.howandev.nexus.configuration.Serializer;
import me.howandev.nexus.configuration.serializer.SerializerRegistry;

import java.lang.reflect.Type;

//Json to object
public class JsonDeserializer implements com.google.gson.JsonDeserializer<Object> {
    private static final Gson gson = new GsonBuilder()
            .create();

    @Override
    public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        Object object = gson.fromJson(jsonElement.getAsJsonObject(), Object.class);
        Serializer<?> serializer = SerializerRegistry.serializerFor(object);
        if (serializer != null) {
            return serializer.deserialize(object);
        }

        return null;
    }
}
