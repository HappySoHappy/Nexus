package me.howandev.nexus.configuration.impl.file.yaml;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class YamlConfigurationTest {
    String yamlString = """
                        boolean:
                          _true: true
                          _false: false
                        byte: (Byte) 127
                        char: (Char) a
                        double:
                          min: 4.9E-324d
                          max: 1.7976931348623157E308d
                        float:
                          min: 1.4E-45f
                          max: 3.4028235E38f
                        integer:
                          min: -2147483648
                          max: 2147483647
                        list:
                        - 1
                        - A String
                        - true
                        - 3.14f
                        long: -9223372036854775808
                        short: (Short) -32768
                        string: String Value
                        """;
    YamlConfiguration configuration;
    @BeforeEach
    void setUp() throws IOException {
        File file = File.createTempFile( "YamlConfigurationTest", "yaml");
        file.deleteOnExit();

        configuration = new YamlConfiguration(file);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void loadFromString() {
        configuration.loadFromString(yamlString);

        Map<String, Object> expectedValues = new LinkedHashMap<>();
        expectedValues.put("boolean", configuration.getSection("boolean"));
        expectedValues.put("boolean._true", true);
        expectedValues.put("boolean._false", false);

        expectedValues.put("byte", Byte.MAX_VALUE);
        expectedValues.put("char", 'a');

        expectedValues.put("double", configuration.getSection("double"));
        expectedValues.put("double.min", Double.MIN_VALUE);
        expectedValues.put("double.max", Double.MAX_VALUE);

        expectedValues.put("float", configuration.getSection("float"));
        expectedValues.put("float.min", Float.MIN_VALUE);
        expectedValues.put("float.max", Float.MAX_VALUE);

        expectedValues.put("integer", configuration.getSection("integer"));
        expectedValues.put("integer.min", Integer.MIN_VALUE);
        expectedValues.put("integer.max", Integer.MAX_VALUE);

        expectedValues.put("list", Arrays.asList(1, "A String", true, 3.14f));

        expectedValues.put("long", Long.MIN_VALUE);
        expectedValues.put("short", Short.MIN_VALUE);
        expectedValues.put("string", "String Value");

        for (Map.Entry<String, Object> entry : expectedValues.entrySet()) {
            assertEquals(entry.getValue(), configuration.get(entry.getKey()));
        }

        assertEquals(expectedValues.keySet(), configuration.getKeys(true));
    }

    @Test
    void dumpToString() {
        configuration.set("when.test", "i sleep");
        configuration.set("once.it-breaks", "real shit");
        configuration.set("list", Arrays.asList(1.2f, 2d, "Just a string", 4, true));

        String expected =
                """
                        when:
                          test: i sleep
                        once:
                          it-breaks: real shit
                        list:
                        - 1.2
                        - 2.0
                        - Just a string
                        - 4
                        - true
                        """;

        assertEquals(expected, configuration.dumpToString());
    }
}