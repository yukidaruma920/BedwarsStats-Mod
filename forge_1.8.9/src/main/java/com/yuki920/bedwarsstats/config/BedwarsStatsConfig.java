package com.yuki920.bedwarsstats.config;

import net.minecraftforge.common.config.Configuration;
import java.io.File;
import java.util.Arrays;

public class BedwarsStatsConfig {

    public static Configuration config;

    public static String apiKey = "";
    public static String bedwarsMode = "OVERALL";
    public static String myNick = "";
    public static boolean showRankPrefix = true;
    public static String displayOrder = "WINS,FINALS,FKDR,WLR,WS";

    public static void syncConfig(File configFile) {
        config = new Configuration(configFile);
        config.load();

        apiKey = config.getString("apiKey", Configuration.CATEGORY_GENERAL, apiKey, "Your Hypixel API key. Get one by running /api new in-game.");
        bedwarsMode = config.getString("bedwarsMode", Configuration.CATEGORY_GENERAL, bedwarsMode, "The Bedwars mode to display stats for.");
        myNick = config.getString("myNick", Configuration.CATEGORY_GENERAL, myNick, "If you are nicked, enter your nick here to see your own stats.");
        showRankPrefix = config.getBoolean("showRankPrefix", Configuration.CATEGORY_GENERAL, showRankPrefix, "Show player rank prefix in stats display.");
        displayOrder = config.getString("displayOrder", Configuration.CATEGORY_GENERAL, displayOrder, "The order in which to display stats. Comma-separated. Valid options: WINS,FINALS,FKDR,WLR,WS");

        if (config.hasChanged()) {
            config.save();
        }
    }


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
