package com.yuki920.bedwarsstats.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "bedwarsstats")
public class BedwarsStatsConfig implements ConfigData {
    public boolean showHud = true;
    public int hudX = 10;
    public int hudY = 10;
    // ... (将来的にHUDのスケールや色などの設定も追加可能) ...
}