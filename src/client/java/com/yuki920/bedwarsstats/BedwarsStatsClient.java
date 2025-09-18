package com.yuki920.bedwarsstats;

import com.yuki920.bedwarsstats.commands.BwmCommand;
import com.yuki920.bedwarsstats.commands.BwmCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class BedwarsStatsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(BwmCommand::register);

        // Add the API key check on server join
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            HypixelApiHandler.checkApiKeyValidity();
        });

        ClientReceiveMessageEvents.GAME.register((message, signedMessage) -> {
            String text = message.getString();
            if (text.startsWith("ONLINE: ")) {
                // We could add a header here before printing the stats, if desired.
                // For now, just process each player.
                String playersPart = text.substring("ONLINE: ".length());
                String[] playerNames = playersPart.split(", ");
                for (String name : playerNames) {
                    HypixelApiHandler.processPlayer(name.trim());
                }
            }
        });
    }
}
