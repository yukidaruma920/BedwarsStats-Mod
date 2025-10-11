package com.yuki920.bedwarsstats.commands;

import com.yuki920.bedwarsstats.HypixelApiHandler;
import com.yuki920.bedwarsstats.config.BedwarsStatsConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import me.shedaniel.autoconfig.AutoConfig;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.CommandSource;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BwmCommand {

    private static final SuggestionProvider<FabricClientCommandSource> MODE_SUGGESTIONS = (context, builder) -> {
        List<String> suggestions = new ArrayList<>();
        for (BedwarsStatsConfig.BedwarsMode mode : BedwarsStatsConfig.BedwarsMode.values()) {
            suggestions.add(mode.name());
            suggestions.addAll(Arrays.asList(mode.getAliases()));
        }
        return CommandSource.suggestMatching(suggestions, builder);
    };

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
                    .suggests(ONLINE_PLAYER_SUGGESTIONS)
                    .executes(context -> {
                        String username = StringArgumentType.getString(context, "username");
                        HypixelApiHandler.processPlayer(username);
                        return 1;
                    })
                    .then(ClientCommandManager.argument("mode", StringArgumentType.string())
                        .suggests(MODE_SUGGESTIONS)
                        .executes(context -> {
                            String username = StringArgumentType.getString(context, "username");
                            String modeStr = StringArgumentType.getString(context, "mode");
                            try {
                                BedwarsStatsConfig.BedwarsMode mode = BedwarsStatsConfig.BedwarsMode.fromString(modeStr);
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
                    context.getSource().sendFeedback(Text.literal("§cUsage: /bwm settings <apikey|mode|nick|showrank|displayorder>"));
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
                            String modeStr = StringArgumentType.getString(context, "mode");
                            try {
                                BedwarsStatsConfig.BedwarsMode mode = BedwarsStatsConfig.BedwarsMode.fromString(modeStr);
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
                .then(ClientCommandManager.literal("showrank")
                    .then(ClientCommandManager.argument("enabled", BoolArgumentType.bool())
                        .executes(context -> {
                            boolean enabled = BoolArgumentType.getBool(context, "enabled");
                            BedwarsStatsConfig config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
                            config.showRankPrefix = enabled;
                            AutoConfig.getConfigHolder(BedwarsStatsConfig.class).save();
                            context.getSource().sendFeedback(Text.literal("§aRank prefix display set to: " + enabled));
                            return 1;
                        })
                    )
                )
                .then(ClientCommandManager.literal("displayorder")
                    .then(ClientCommandManager.argument("order", StringArgumentType.greedyString())
                        .executes(context -> {
                            String orderStr = StringArgumentType.getString(context, "order");
                            String[] stats = orderStr.split(",");
                            List<String> validStats = new ArrayList<>();
                            for (String stat : stats) {
                                try {
                                    BedwarsStatsConfig.Stat.valueOf(stat.trim().toUpperCase());
                                    validStats.add(stat.trim().toUpperCase());
                                } catch (IllegalArgumentException e) {
                                    context.getSource().sendError(Text.literal("§cInvalid stat: " + stat));
                                    return 0;
                                }
                            }
                            BedwarsStatsConfig config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
                            config.displayOrder = String.join(",", validStats);
                            AutoConfig.getConfigHolder(BedwarsStatsConfig.class).save();
                            context.getSource().sendFeedback(Text.literal("§aDisplay order set to: " + config.displayOrder));
                            return 1;
                        })
                    )
                )
            )
        );
    }
}