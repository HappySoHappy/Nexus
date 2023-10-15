package me.howandev.nexus.configuration.impl;

import me.howandev.nexus.configuration.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MemoryConfigurationTest {
    private Configuration configuration = null;

    @BeforeEach
    void setUp() {
        configuration = new MemoryConfiguration();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testNoArgsConstuctor() {
        assertNull(configuration.getParent());
        assertSame(configuration, configuration.getRoot());
        assertEquals("", configuration.getPath());
        assertEquals("", configuration.getFullPath());
    }

    @Test
    void testParentConstructor() {
        Configuration config = new MemoryConfiguration(configuration);

        assertSame(configuration, config.getParent());
        assertSame(configuration.getRoot(), config.getRoot());
        assertEquals("", config.getPath());
        assertEquals("", config.getFullPath());
    }

    @Test
    void testParentPathConstuctor() {
        Configuration config = new MemoryConfiguration(configuration, "custom.path");

        assertSame(configuration, config.getParent());
        assertSame(configuration.getRoot(), config.getRoot());
        assertEquals("custom.path", config.getPath());
        assertEquals("custom.path", config.getFullPath());

        Configuration config2 = new MemoryConfiguration(config, "with.multiple-paths");
        assertSame(config, config2.getParent());
        assertSame(config.getRoot(), config2.getRoot());
        assertEquals("with.multiple-paths", config2.getPath());
        assertEquals("custom.path.with.multiple-paths", config2.getFullPath());
    }

    @Test
    void testSetGetMethods() {
        Map<String, Object> expectedStore = new HashMap<>(){{
            put("null", null);
            put("true-boolean", true);
            put("false-boolean", false);
            put("char", 'a');
            put("float", 3.14);
            put("double", 3.14159265d);
            put("integer", Integer.MIN_VALUE);
            put("list", Arrays.asList(1, null, true, 4, "Hello!"));
            put("long", Long.MAX_VALUE);
            put("map", new HashMap<>(){{
                put("null", true);
                put("0", "It's still a string...");
            }});
            put("enum", EnumConstants.CONST_1);
            put("string", "Nexus Configuration is the best!");
        }};

        expectedStore.forEach((key, value) -> {
            configuration.set(key, value);
        });

        assertNull(configuration.get("null"));
        assertEquals("Default Value", configuration.get("null", "Default Value"));
        assertTrue(configuration.getBoolean("true-boolean", false));
        assertFalse(configuration.getBoolean("false-boolean", true));
        assertEquals('a', configuration.getCharacter("char"));
        assertEquals(3.14f, configuration.getFloat("float"));
        assertEquals(3.14159265d, configuration.getDouble("double"));
        assertEquals(Integer.MIN_VALUE, configuration.getDouble("integer"));
        assertIterableEquals(Arrays.asList(1, null, true, 4, "Hello!"), configuration.getList("list"));
        assertEquals(Long.MAX_VALUE, configuration.getDouble("long"));

        assertMapEquals(new HashMap<>(){{
            put("null", true);
            put("0", "It's still a string...");
        }}, configuration.getMap("map"));

        assertEquals(EnumConstants.CONST_1, configuration.getEnumConstant(EnumConstants.class, "enum"));

        assertEquals("Nexus Configuration is the best!", configuration.getString("string"));
    }

    private enum EnumConstants {
        CONST_1,
    }

    @Test
    void testGetEnumAsString() {
        configuration.set("enum", EnumConstants.CONST_1);
        configuration.set("string", "CONST_1");

        assertEquals("CONST_1", configuration.getString("enum"));
        assertEquals(EnumConstants.CONST_1, configuration.getEnumConstant(EnumConstants.class, "string"));
    }

    @Test
    void testGetFloatingPointAsInteger() {
        configuration.set("float", 3.14f);
        configuration.set("double", 3.14159265d);

        assertEquals(3, configuration.getInteger("float"));
        assertEquals(3, configuration.getInteger("double"));
    }

    @Test
    void testGetIntegerAsFloatingPoint() {
        configuration.set("integer", 3);

        assertEquals(3.0f, configuration.getFloat("integer"));
        assertEquals(3.0d, configuration.getDouble("integer"));
    }


    @Test
    void testCreateSection() {
        Set<String> set = Set.of("this", "this.test.sub", "this.test", "this.test.other");

        configuration.createSection("this.test.sub");
        configuration.createSection("this.test.other");

        assertEquals(set, configuration.getKeys(true));
    }

    public static void assertMapEquals(Map<?, ?> expected, Map<?, ?> actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        //Yeah, no shit sherlock...
        assertTrue(actual.keySet().containsAll(expected.keySet()));

        expected.keySet().forEach((key) -> {
            assertEquals(expected.get(key), actual.get(key));
        });
    }
}
