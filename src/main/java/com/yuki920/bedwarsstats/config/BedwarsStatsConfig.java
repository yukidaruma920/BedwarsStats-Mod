package com.yuki920.bedwarsstats.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "bedwarsstats")
public class BedwarsStatsConfig implements ConfigData {

    @Comment("Your Hypixel API key. Get one by running /api new in-game.")
    public String apiKey = "";

}