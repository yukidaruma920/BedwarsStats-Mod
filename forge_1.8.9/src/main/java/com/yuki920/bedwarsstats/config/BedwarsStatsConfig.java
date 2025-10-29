package com.yuki920.bedwarsstats.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import java.io.File;
import java.util.Arrays;

public class BedwarsStatsConfig {

    public static final String CATEGORY_GENERAL = "general";
    private static Configuration config;

    // Default values
    private static final String DEFAULT_API_KEY = "";
    private static final String DEFAULT_BEDWARS_MODE = "OVERALL";
    private static final String DEFAULT_MY_NICK = "";
    private static final boolean DEFAULT_SHOW_RANK_PREFIX = true;
    private static final String DEFAULT_DISPLAY_ORDER = "WINS,FINALS,FKDR,WLR,WS";

    public static void syncConfig(File configFile) {
        if (config == null) {
            config = new Configuration(configFile);
        }
        config.load();

        // This will create the properties if they don't exist and load the values.
        config.getString("apiKey", CATEGORY_GENERAL, DEFAULT_API_KEY, "Your Hypixel API key. Get one by running /api new in-game.");
        config.getString("bedwarsMode", CATEGORY_GENERAL, DEFAULT_BEDWARS_MODE, "The Bedwars mode to display stats for.");
        config.getString("myNick", CATEGORY_GENERAL, DEFAULT_MY_NICK, "If you are nicked, enter your nick here to see your own stats.");
        config.getBoolean("showRankPrefix", CATEGORY_GENERAL, DEFAULT_SHOW_RANK_PREFIX, "Show player rank prefix in stats display.");
        config.getString("displayOrder", CATEGORY_GENERAL, DEFAULT_DISPLAY_ORDER, "The order in which to display stats. Comma-separated. Valid options: WINS,FINALS,FKDR,WLR,WS");

        if (config.hasChanged()) {
            config.save();
        }
    }

    // Getters
    public static String getApiKey() { return config.getString("apiKey", CATEGORY_GENERAL, DEFAULT_API_KEY, ""); }
    public static String getBedwarsMode() { return config.getString("bedwarsMode", CATEGORY_GENERAL, DEFAULT_BEDWARS_MODE, ""); }
    public static String getMyNick() { return config.getString("myNick", CATEGORY_GENERAL, DEFAULT_MY_NICK, ""); }
    public static boolean getShowRankPrefix() { return config.getBoolean("showRankPrefix", CATEGORY_GENERAL, DEFAULT_SHOW_RANK_PREFIX, ""); }
    public static String getDisplayOrder() { return config.getString("displayOrder", CATEGORY_GENERAL, DEFAULT_DISPLAY_ORDER, ""); }

    // Setters
    private static void set(String name, String value) {
        Property prop = config.get(CATEGORY_GENERAL, name, "");
        prop.set(value);
        config.save();
    }

    private static void set(String name, boolean value) {
        Property prop = config.get(CATEGORY_GENERAL, name, false);
        prop.set(value);
        config.save();
    }

    public static void setApiKey(String value) { set("apiKey", value); }
    public static void setBedwarsMode(String value) { set("bedwarsMode", value); }
    public static void setMyNick(String value) { set("myNick", value); }
    public static void setShowRankPrefix(boolean value) { set("showRankPrefix", value); }
    public static void setDisplayOrder(String value) { set("displayOrder", value); }


    public enum Stat {
        WINS, FINALS, FKDR, WLR, WS
    }

    public enum BedwarsMode {
        OVERALL("Overall", "", "all"),
        SOLO("Solo", "eight_one_", "1s"),
        DOUBLES("Doubles", "eight_two_", "2s"),
        THREES("Threes", "four_three_", "3s"),
        FOURS("Fours", "four_four_", "4s"),
        FOUR_V_FOUR("4v4", "two_four_", "4v4");

        private final String displayName;
        private final String apiPrefix;
        private final String[] aliases;

        BedwarsMode(String displayName, String apiPrefix, String... aliases) {
            this.displayName = displayName;
            this.apiPrefix = apiPrefix;
            this.aliases = aliases;
        }

        public static BedwarsMode fromString(String text) {
            for (BedwarsMode mode : values()) {
                if (mode.name().equalsIgnoreCase(text)) return mode;
                for (String alias : mode.aliases) {
                    if (alias.equalsIgnoreCase(text)) return mode;
                }
            }
            // Default to OVERALL if not found
            return OVERALL;
        }

        public String getDisplayName() { return displayName; }
        public String getApiPrefix() { return apiPrefix; }
        public String[] getAliases() { return aliases; }

        @Override
        public String toString() {
            return displayName;
        }
    }
}
