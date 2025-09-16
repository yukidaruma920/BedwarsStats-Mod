package com.yuki920.bedwarsstats;

import com.yuki920.bedwarsstats.commands.BwmCommand; // ★★★ この行を追加
import com.yuki920.bedwarsstats.HypixelApiHandler;
import com.yuki920.bedwarsstats.hud.HudData;
import com.yuki920.bedwarsstats.hud.WhoHud;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback; // ★★★ CommandRegistrationCallbackから変更
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class BedwarsStatsClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
	        // ★★★ ここにコマンド登録を追加 ★★★
        ClientCommandRegistrationCallback.EVENT.register(BwmCommand::register);

        // ゲームチャットメッセージを受信したときのイベントを登録
        ClientReceiveMessageEvents.GAME.register((message, signedMessage) -> {
            String text = message.getString();
            if (text.startsWith("ONLINE: ")) {
                HudData.getInstance().clear(); // Clear previous data
                String playersPart = text.substring("ONLINE: ".length());
                String[] playerNames = playersPart.split(", ");
                for (String name : playerNames) {
                    HypixelApiHandler.processPlayer(name.trim(), true);
                }
            }
        });

        HudRenderCallback.EVENT.register(new WhoHud());
    }
}
