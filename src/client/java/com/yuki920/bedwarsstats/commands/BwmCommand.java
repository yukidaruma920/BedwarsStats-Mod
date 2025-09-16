package com.yuki920.bedwarsstats.commands;

import com.yuki920.bedwarsstats.HypixelApiHandler; // ★★★ この行を追加
import com.yuki920.bedwarsstats.config.BedwarsStatsConfig;
import com.yuki920.bedwarsstats.hud.HudEditorScreen;
import com.mojang.brigadier.CommandDispatcher;
import me.shedaniel.autoconfig.AutoConfig;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager; // ★★★ CommandManagerから変更
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource; // ★★★ ServerCommandSourceから変更
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.text.Text;

public class BwmCommand {
    // ★★★ メソッドの引数の型を変更 ★★★
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("bwm")
            // /bwm (引数なし)
            .executes(context -> {
                context.getSource().sendFeedback(Text.literal("§cUsage: /bwm <setapikey|stats>"));
                return 1;
            })
            // /bwm setapikey <key>
            .then(ClientCommandManager.literal("setapikey")
                .then(ClientCommandManager.argument("key", StringArgumentType.greedyString())
                    .executes(context -> {
                        String apiKey = StringArgumentType.getString(context, "key");
                        BedwarsStatsConfig config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
                        config.apiKey = apiKey;
                        AutoConfig.getConfigHolder(BedwarsStatsConfig.class).save();
                        context.getSource().sendFeedback(Text.literal("§aAPI Key set successfully!"));
                        return 1;
                    })
                )
            )
            // ★★★ ここからが新しいサブコマンド ★★★
            // /bwm stats <username>
            .then(ClientCommandManager.literal("stats")
                .then(ClientCommandManager.argument("username", StringArgumentType.string())
                    .executes(context -> {
                        String username = StringArgumentType.getString(context, "username");
                        // HypixelApiHandlerを直接呼び出す
                        HypixelApiHandler.processPlayer(username);
                        return 1;
                    })
                )
            )
            // /bwm hud
            .then(ClientCommandManager.literal("hud")
                .executes(context -> {
                    context.getSource().getClient().execute(() -> {
                        context.getSource().getClient().setScreen(new HudEditorScreen());
                    });
                    return 1;
                })
            )
        );
    }
}
