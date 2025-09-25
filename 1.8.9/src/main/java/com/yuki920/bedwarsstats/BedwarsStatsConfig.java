package com.yuki920.bedwarsstats;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class BedwarsStatsConfig {

    private static Configuration config;

    public static String apiKey = "";
    public static String myNick = "";
    public static BedwarsMode bedwarsMode = BedwarsMode.OVERALL;

    public enum BedwarsMode {
        OVERALL("Overall", ""),
        SOLO("Solo", "eight_one_"),
        DOUBLES("Doubles", "eight_two_"),
        THREES("Threes", "four_three_"),
        FOURS("Fours", "four_four_");

        private final String displayName;
        private final String apiPrefix;

        BedwarsMode(String displayName, String apiPrefix) {
            this.displayName = displayName;
            this.apiPrefix = apiPrefix;
        }

        public String getDisplayName() {
            return displayName;
        }

        public String getApiPrefix() {
            return apiPrefix;
        }
    }

    public static void init(File configFile) {
        config = new Configuration(configFile);
        syncConfig();
    }

    public static void syncConfig() {
        config.load();

        apiKey = config.getString("apiKey", Configuration.CATEGORY_GENERAL, "", "Your Hypixel API key.");
        myNick = config.getString("myNick", Configuration.CATEGORY_GENERAL, "", "If you are nicked, enter your nick here to see your own stats.");

        String modeString = config.getString("bedwarsMode", Configuration.CATEGORY_GENERAL, "OVERALL", "The Bedwars mode to display stats for.",
                new String[]{"OVERALL", "SOLO", "DOUBLES", "THREES", "FOURS"});
        try {
            bedwarsMode = BedwarsMode.valueOf(modeString.toUpperCase());
        } catch (IllegalArgumentException e) {
            bedwarsMode = BedwarsMode.OVERALL;
        }

        if (config.hasChanged()) {
            config.save();
        }
    }

    public static void setApiKey(String newApiKey) {
        apiKey = newApiKey;
        config.get(Configuration.CATEGORY_GENERAL, "apiKey", "").set(newApiKey);
        config.save();
    }

    public static void setMyNick(String newNick) {
        myNick = newNick;
        config.get(Configuration.CATEGORY_GENERAL, "myNick", "").set(newNick);
        config.save();
    }

    public static void setBedwarsMode(BedwarsMode newMode) {
        bedwarsMode = newMode;
        config.get(Configuration.CATEGORY_GENERAL, "bedwarsMode", "OVERALL").set(newMode.name());
        config.save();
    }
}