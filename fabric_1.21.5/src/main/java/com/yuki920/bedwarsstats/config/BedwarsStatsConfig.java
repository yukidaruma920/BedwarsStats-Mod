package com.yuki920.bedwarsstats.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import java.util.Arrays;

@Config(name = "bedwarsstats")
public class BedwarsStatsConfig implements ConfigData {

    @Comment("Your Hypixel API key. Get one by running /api new in-game.")
    public String apiKey = "";

    @ConfigEntry.Gui.EnumHandler(option = ConfigEntry.Gui.EnumHandler.EnumDisplayOption.BUTTON)
    @Comment("The Bedwars mode to display stats for.")
    public BedwarsMode bedwarsMode = BedwarsMode.OVERALL;

    @Comment("If you are nicked, enter your nick here to see your own stats.")
    public String myNick = "";

    @Comment("Show player rank prefix in stats display.")
    public boolean showRankPrefix = true;

    @Comment("The order in which to display stats. Comma-separated. Valid options: WINS,FINALS,FKDR,WLR,WS")
    public String displayOrder = "WINS,FINALS,FKDR,WLR,WS";

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
            throw new IllegalArgumentException("No enum constant " + text);
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