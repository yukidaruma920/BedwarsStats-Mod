package com.yuki920.bedwarsstats.config;

import com.yuki920.bedwarsstats.BedwarsStatsMod;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class ConfigHandler {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static File configFile;
    private static ConfigData config;

    // 設定データを保持するクラス
    private static class ConfigData {
        String apiKey = "";
    }

    public static void loadConfig() {
        Path configDir = FabricLoader.getInstance().getConfigDir();
        configFile = new File(configDir.toFile(), "bedwarsstats.json");

        if (configFile.exists()) {
            try (FileReader reader = new FileReader(configFile)) {
                config = GSON.fromJson(reader, ConfigData.class);
            } catch (IOException e) {
                BedwarsStatsMod.LOGGER.error("Could not read config file!", e);
                config = new ConfigData();
            }
        } else {
            config = new ConfigData();
            saveConfig();
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(configFile)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            BedwarsStatsMod.LOGGER.error("Could not write config file!", e);
        }
    }

    public static String getApiKey() {
        return config.apiKey;
    }

    public static void setApiKey(String key) {
        config.apiKey = key;
    }
}
