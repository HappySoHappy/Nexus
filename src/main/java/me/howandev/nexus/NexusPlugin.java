package me.howandev.nexus;

import lombok.Getter;
import me.howandev.nexus.command.manager.BukkitCommandManager;
import me.howandev.nexus.command.manager.BukkitKitManager;
import me.howandev.nexus.command.sender.BukkitSenderFactory;
import me.howandev.nexus.configuration.Configuration;
import me.howandev.nexus.configuration.impl.file.FileConfiguration;
import me.howandev.nexus.configuration.impl.file.yaml.YamlConfiguration;
import me.howandev.nexus.glow.GlowManager;
import me.howandev.nexus.integration.IntegrationManager;
import me.howandev.nexus.listener.AsyncChatListener;
import me.howandev.nexus.locale.TranslationManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Getter
public class NexusPlugin extends JavaPlugin {
    @Getter
    private static NexusPlugin instance = null;
    @Getter
    private static Logger pluginLogger;
    private static FileConfiguration fileConfiguration;
    private FileConfiguration economyStorage;
    @Getter
    private static NexusConfiguration configuration;
    @Getter
    private static Map<String, NexusConfiguration> groupConfiguration;
    private TranslationManager translationManager;
    private IntegrationManager integrationManager;
    private BukkitKitManager kitsManager;
    @Getter
    private BukkitCommandManager commandManager;

    private GlowManager glowManager;

    public NexusPlugin() {
        super();
        instance = this;
        pluginLogger = getLogger();
        fileConfiguration = new YamlConfiguration(new File(getDataFolder(), "config.yml"));
        economyStorage = new YamlConfiguration(new File(getDataFolder(), "economy.yml"));
        configuration = new NexusConfiguration(fileConfiguration);
        groupConfiguration = new HashMap<>();
    }

    @Override
    public void onEnable() {
        setupConfiguration();

        translationManager = new TranslationManager(this);
        translationManager.reload();

        integrationManager = new IntegrationManager(this);
        integrationManager.initializeIntegrations();

        kitsManager = new BukkitKitManager(this);
        kitsManager.loadKits();

        BukkitSenderFactory senderFactory = new BukkitSenderFactory(this);
        commandManager = new BukkitCommandManager(this, senderFactory);
        commandManager.registerAll();

        startEconomySavingLoop(5, TimeUnit.MINUTES);

        setupListeners();
        Bukkit.getPluginManager().registerEvents(commandManager, this);

        glowManager = new GlowManager();
    }

    private ScheduledExecutorService scheduler;
    public void startEconomySavingLoop(long interval, TimeUnit unit) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                economyStorage.save();
            } catch (IOException ignored) { }
        }, 0, interval, unit);
    }

    @Override
    public void onDisable() {
        try {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();
                try {
                    if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    scheduler.shutdownNow();
                }
            }

            economyStorage.save();
        } catch (Exception ex) {
            getLogger().warning("An exception has occured during saving economy, data loss will occur.");
            ex.printStackTrace();
        }

        try {
            kitsManager.saveKits();
        } catch (Exception ex) {
            getLogger().warning("Unable to save kits data loss may occur.");
            ex.printStackTrace();
        }
    }

    public void setupConfiguration() {
        saveDefaultConfig();

        try {
            fileConfiguration.load();

            var economy = new File(getDataFolder(), "economy.yml");
            if (economy.exists() && economy.isDirectory()) {
                economy.delete();
            }

            if (!economy.exists())
                economy.createNewFile();

            economyStorage.load();

            configuration = new NexusConfiguration(fileConfiguration);
            configuration.load();

            Configuration groupOverwriteSection = fileConfiguration.getSection("group-overwrite");
            if (groupOverwriteSection != null)
                for (String group : groupOverwriteSection.getKeys(false)) {
                    NexusConfiguration subConfiguration =
                            new NexusConfiguration(fileConfiguration.getSection("group-overwrite."+group));

                    subConfiguration.load();
                    groupConfiguration.put(group, subConfiguration);
                }
        } catch (IOException ex) {
            throw new RuntimeException("Unable to load plugin configuration file!", ex);
        }
    }

    private void setupListeners() {
        Bukkit.getPluginManager().registerEvents(new AsyncChatListener(), this);
    }
}
