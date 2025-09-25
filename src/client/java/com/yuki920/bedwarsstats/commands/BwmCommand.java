package com.yuki920.bedwarsstats.commands;

import com.yuki920.bedwarsstats.HypixelApiHandler;
import com.yuki920.bedwarsstats.config.BedwarsStatsConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.shedaniel.autoconfig.AutoConfig;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.Arrays;

public class BwmCommand {

    private static final SuggestionProvider<FabricClientCommandSource> MODE_SUGGESTIONS = (context, builder) ->
            CommandSource.suggestMatching(
                    Arrays.stream(BedwarsStatsConfig.BedwarsMode.values()).map(Enum::name),
                    builder
            );

    private static final SuggestionProvider<FabricClientCommandSource> ONLINE_PLAYER_SUGGESTIONS = (context, builder) ->
            CommandSource.suggestMatching(context.getSource().getPlayerNames(), builder);


    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
        dispatcher.register(ClientCommandManager.literal("bwm")
            .executes(context -> {
                context.getSource().sendFeedback(Text.literal("§cUsage: /bwm <stats|settings>"));
                return 1;
            })
            .then(ClientCommandManager.literal("stats")
                .then(ClientCommandManager.argument("username", StringArgumentType.string())
                    .suggests(ONLINE_PLAYER_SUGGESTIONS) // Suggest online players
                    .executes(context -> {
                        String username = StringArgumentType.getString(context, "username");
                        // No mode specified, use the one from config
                        HypixelApiHandler.processPlayer(username);
                        return 1;
                    })
                    .then(ClientCommandManager.argument("mode", StringArgumentType.string())
                        .suggests(MODE_SUGGESTIONS)
                        .executes(context -> {
                            String username = StringArgumentType.getString(context, "username");
                            String modeStr = StringArgumentType.getString(context, "mode").toUpperCase();
                            try {
                                BedwarsStatsConfig.BedwarsMode mode = BedwarsStatsConfig.BedwarsMode.valueOf(modeStr);
                                // Mode specified, use it for this lookup
                                HypixelApiHandler.processPlayer(username, mode);
                            } catch (IllegalArgumentException e) {
                                context.getSource().sendFeedback(Text.literal("§cInvalid mode. Use tab-completion for suggestions."));
                            }
                            return 1;
                        })
                    )
                )
            )
            .then(ClientCommandManager.literal("settings")
                .executes(context -> {
                    context.getSource().sendFeedback(Text.literal("§cUsage: /bwm settings <apikey|mode|nick>"));
                    return 1;
                })
                .then(ClientCommandManager.literal("apikey")
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
                .then(ClientCommandManager.literal("mode")
                    .then(ClientCommandManager.argument("mode", StringArgumentType.string())
                        .suggests(MODE_SUGGESTIONS)
                        .executes(context -> {
                            String modeStr = StringArgumentType.getString(context, "mode").toUpperCase();
                            try {
                                BedwarsStatsConfig.BedwarsMode mode = BedwarsStatsConfig.BedwarsMode.valueOf(modeStr);
                                BedwarsStatsConfig config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
                                config.bedwarsMode = mode;
                                AutoConfig.getConfigHolder(BedwarsStatsConfig.class).save();
                                context.getSource().sendFeedback(Text.literal("§aBedwars mode set to " + mode.getDisplayName()));
                            } catch (IllegalArgumentException e) {
                                context.getSource().sendFeedback(Text.literal("§cInvalid mode. Use tab-completion for suggestions."));
                            }
                            return 1;
                        })
                    )
                )
                .then(ClientCommandManager.literal("nick")
                    .then(ClientCommandManager.argument("nick", StringArgumentType.string())
                        .executes(context -> {
                            String nick = StringArgumentType.getString(context, "nick");
                            BedwarsStatsConfig config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
                            config.myNick = nick;
                            AutoConfig.getConfigHolder(BedwarsStatsConfig.class).save();
                            context.getSource().sendFeedback(Text.literal("§aYour nick has been set to: " + nick));
                            return 1;
                        })
                    )
                )
            )
        );
    }
}