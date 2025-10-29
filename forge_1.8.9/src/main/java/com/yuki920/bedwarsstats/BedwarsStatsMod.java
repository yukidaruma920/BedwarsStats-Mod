package com.yuki920.bedwarsstats;

import com.yuki920.bedwarsstats.commands.BwmCommand;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import com.yuki920.bedwarsstats.config.BedwarsStatsConfig;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = BedwarsStatsMod.MOD_ID, version = BedwarsStatsMod.VERSION, name = BedwarsStatsMod.NAME, guiFactory = "com.yuki920.bedwarsstats.config.BedwarsStatsGuiFactory")
public class BedwarsStatsMod {
    public static final String MOD_ID = "bedwarsstats";
    public static final String VERSION = "1.4.2";
    public static final String NAME = "Bedwars Stats";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        BedwarsStatsConfig.syncConfig(event.getSuggestedConfigurationFile());
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("Bedwars Stats Mod Initializing...");

        // Register event handlers
        MinecraftForge.EVENT_BUS.register(this);

        // Register commands
        ClientCommandHandler.instance.registerCommand(new BwmCommand());
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        // type 0 is standard chat message
        if (event.type == 0) {
            String text = event.message.getUnformattedText();
            if (text.startsWith("ONLINE: ")) {
                String playersPart = text.substring("ONLINE: ".length());
                String[] playerNames = playersPart.split(", ");
                for (String name : playerNames) {
                    HypixelApiHandler.processPlayer(name.trim());
                }
            }
        }
    }
}
