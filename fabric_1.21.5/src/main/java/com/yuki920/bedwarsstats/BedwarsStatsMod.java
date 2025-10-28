package com.yuki920.bedwarsstats;

import com.yuki920.bedwarsstats.config.BedwarsStatsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BedwarsStatsMod implements ModInitializer {
    public static final String MOD_ID = "bedwarsstats";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Bedwars Stats Mod Initializing...");

        // Register our config class with AutoConfig
        AutoConfig.register(BedwarsStatsConfig.class, GsonConfigSerializer::new);
    }
}
