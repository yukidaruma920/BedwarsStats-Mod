package com.yuki920.bedwarsstats;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.command.ICommand;
import net.minecraftforge.client.ClientCommandHandler;

@Mod(modid = BedwarsStatsMod.MOD_ID, name = BedwarsStatsMod.NAME, version = BedwarsStatsMod.VERSION)
public class BedwarsStatsMod {

    public static final String MOD_ID = "bedwarsstats";
    public static final String NAME = "BedwarsStats";
    public static final String VERSION = "1.0.0";

    @Mod.Instance(MOD_ID)
    public static BedwarsStatsMod instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        BedwarsStatsConfig.init(event.getSuggestedConfigurationFile());
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        ClientCommandHandler.instance.registerCommand(new BwmCommand());
    }
}