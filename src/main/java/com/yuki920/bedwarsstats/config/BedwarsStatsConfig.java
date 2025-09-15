package com.yuki920.bedwarsstats.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "bedwarsstats")
public class BedwarsStatsConfig implements ConfigData {

    @ConfigEntry.Category("nametag")
    @ConfigEntry.Gui.TransitiveObject
    @Comment("Settings for the level display on nametags")
    public NametagSettings nametag = new NametagSettings();

    @ConfigEntry.Category("who_hud")
    @ConfigEntry.Gui.TransitiveObject
    @Comment("Settings for the /who command HUD")
    public WhoHudSettings whoHud = new WhoHudSettings();

    public static class NametagSettings {
        @Comment("Enable/Disable the Bedwars level display on player nametags")
        public boolean showNametagLevel = true;
    }

    public static class WhoHudSettings {
        @Comment("Enable/Disable the HUD that shows stats from /who")
        public boolean showWhoHud = true;

        @ConfigEntry.Gui.Excluded
        public int hudX = 10;

        @ConfigEntry.Gui.Excluded
        public int hudY = 10;

        @ConfigEntry.Gui.Excluded
        public int hudWidth = 250;

        @ConfigEntry.Gui.Excluded
        public int hudHeight = 150;
    }
}