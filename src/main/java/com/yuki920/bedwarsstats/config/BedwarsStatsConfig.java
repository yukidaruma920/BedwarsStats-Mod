package com.yuki920.bedwarsstats.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "bedwarsstats")
public class BedwarsStatsConfig implements ConfigData {

    @Comment("Your Hypixel API key. Get one by running /api new in-game.")
    public String apiKey = "";

    @ConfigEntry.Category("who_hud")
    @ConfigEntry.Gui.TransitiveObject
    @Comment("Settings for the /who command HUD")
    public WhoHudSettings whoHud = new WhoHudSettings();

    public static class WhoHudSettings {
        @Comment("Enable/Disable the HUD that shows stats from /who")
        public boolean showWhoHud = true;

        @Comment("Enable/Disable the shadow on the HUD text")
        public boolean textShadow = true;

        @Comment("Change the overall size of the HUD (in percent)")
        @ConfigEntry.BoundedDiscrete(min = 50, max = 300)
        public int hudScalePercent = 100;

        @ConfigEntry.Gui.Excluded
        public int hudX = 10;

        @ConfigEntry.Gui.Excluded
        public int hudY = 10;
    }
}