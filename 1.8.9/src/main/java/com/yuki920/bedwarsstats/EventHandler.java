package com.yuki920.bedwarsstats;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class EventHandler {

    @SubscribeEvent
    public void onChatReceived(ClientChatReceivedEvent event) {
        // type 0 is a standard chat message.
        if (event.type == 0) {
            String text = event.message.getUnformattedText();
            if (text.startsWith("ONLINE: ")) {
                event.setCanceled(true); // Hide the original "ONLINE: ..." message
                String playersPart = text.substring("ONLINE: ".length());
                String[] playerNames = playersPart.split(", ");
                for (String name : playerNames) {
                    HypixelApiHandler.processPlayer(name.trim());
                }
            }
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        // Check API key validity shortly after joining a server.
        // A small delay can prevent potential issues with initialization order.
        new Thread(() -> {
            try {
                Thread.sleep(2000); // 2-second delay
                HypixelApiHandler.checkApiKeyValidity();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}