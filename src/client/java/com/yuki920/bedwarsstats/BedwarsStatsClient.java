package com.yuki920.bedwarsstats;

import com.yuki920.bedwarsstats.commands.BwmCommand;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;

public class BedwarsStatsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(BwmCommand::register);

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
