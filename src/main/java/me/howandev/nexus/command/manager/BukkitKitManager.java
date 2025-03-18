package me.howandev.nexus.command.manager;

import me.howandev.nexus.kit.Kit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class BukkitKitManager {
    private JavaPlugin plugin;
    private FileConfiguration kitsConfiguration; // bukkit config xdxd

    private Map<String, Kit> loadedKits = new HashMap<>();
    public BukkitKitManager(JavaPlugin plugin) {
        this.plugin = plugin;
        kitsConfiguration = new YamlConfiguration();
    }

    public void createKit(Kit kit) {
        loadedKits.put(kit.getName().toLowerCase(), kit);
    }

    public Kit getKit(String name) {
        return loadedKits.get(name.toLowerCase());
    }

    public void loadKits() {
        try {
            var kits = new File(plugin.getDataFolder(), "kits.yml");
            if (!kits.exists())
                kits.createNewFile();

            kitsConfiguration.load(kits);
        } catch (Exception ex) {
            plugin.getLogger().warning("Unable to create kits file, kits will not be loaded.");
            ex.printStackTrace();
        }

        for (String name : kitsConfiguration.getKeys(false)) {
            var kit = new Kit(name.toLowerCase());

            var keys = kitsConfiguration.getConfigurationSection(name + ".items").getKeys(false);
            for (String key : keys) {
                var item = kitsConfiguration.getItemStack(name + ".items." + key);
                if (item == null || item.getType().isAir()) continue;

                kit.addItem(item);
            }
        }
    }

    public void saveKits() {
        try {
            for (Map.Entry<String, Kit> entry : loadedKits.entrySet()) {
                for (int i = 0; i < entry.getValue().getItems().size(); i++) {
                    var item = (ItemStack) entry.getValue().getItems().toArray()[i];
                    if (item == null || item.getType().isAir()) continue;
                    kitsConfiguration.set(entry.getKey().toLowerCase() + ".items." + i, item);
                }
            }

            var kits = new File(plugin.getDataFolder(), "kits.yml");
            if (!kits.exists())
                kits.createNewFile();
            kitsConfiguration.save(kits);
        } catch (Exception ex) {
            plugin.getLogger().warning("Unable to save kits, data loss may occur.");
            ex.printStackTrace();
        }
    }
}
