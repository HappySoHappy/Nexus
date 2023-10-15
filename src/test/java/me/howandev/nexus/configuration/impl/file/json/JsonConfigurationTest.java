package me.howandev.nexus.configuration.impl.file.json;

import me.howandev.nexus.configuration.impl.MemoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JsonConfigurationTest {
    String jsonString = """
                {
                  "array": [
                    1,
                    2.5,
                    true
                  ],
                  "==": "Vecc",
                  "color": "gold",
                  "null": null,
                  "number": 123,
                  "object": {
                    "a": "b",
                    "c": "d"
                  },
                  "string": "Hello World"
                }""";
    JsonConfiguration configuration;
    @BeforeEach
    void setup() throws IOException {
        File file = File.createTempFile( "JsonConfigurationTest", "json");
        file.deleteOnExit();

        configuration = new JsonConfiguration(file);
    }

    @Test
    void loadFromString() {
        configuration.loadFromString(jsonString);


        System.out.println("keys: "+configuration.getKeys(true));

        System.out.println("listing values... ");
        for (Map.Entry<String, Object> entry : configuration.getValues(true).entrySet()) {
            System.out.println(entry.getKey()+": "+entry.getValue());
        }
    }

    @Test
    void dumpToString() {
        configuration.set("array", new Object[]{"two", 2, 5.0f});
        configuration.set("==", "Vecc");
        configuration.set("color", "gold");
        configuration.set("null", null);
        configuration.set("number", 123);

        MemoryConfiguration memory = new MemoryConfiguration();
        memory.set("a", "b");
        memory.set("c", "d");
        configuration.set("config", memory);
        configuration.set("string", "Hello World");

        System.out.println("json dump...");
        System.out.println(configuration.dumpToString());
    }
}