package com.yuki920.bedwarsstats.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

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

    public enum BedwarsMode {
        OVERALL("Overall", ""),
        SOLO("Solo", "eight_one_"),
        DOUBLES("Doubles", "eight_two_"),
        THREES("Threes", "four_three_"),
        FOURS("Fours", "four_four_"),
        FOUR_V_FOUR("4v4", "two_four_");

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

        @Override
        public String toString() {
            return displayName;
        }
    }
}