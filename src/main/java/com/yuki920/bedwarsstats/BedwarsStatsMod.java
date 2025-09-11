package com.yuki920.bedwarsstats;

import com.yuki920.bedwarsstats.config.ConfigHandler;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BedwarsStatsMod implements ModInitializer {
    public static final String MOD_ID = "bedwarsstats";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Bedwars Stats Mod Initializing...");
        ConfigHandler.loadConfig();
    }
}
