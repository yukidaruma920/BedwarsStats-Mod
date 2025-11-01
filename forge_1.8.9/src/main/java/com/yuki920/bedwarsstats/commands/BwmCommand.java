package com.yuki920.bedwarsstats.commands;

import com.yuki920.bedwarsstats.HypixelApiHandler;
import com.yuki920.bedwarsstats.config.BedwarsStatsConfig;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BwmCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "bwm";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/bwm <stats|settings>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: " + getCommandUsage(sender)));
            return;
        }

        String subCommand = args[0];
        if ("stats".equalsIgnoreCase(subCommand)) {
            processStatsCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        } else if ("settings".equalsIgnoreCase(subCommand)) {
            processSettingsCommand(sender, Arrays.copyOfRange(args, 1, args.length));
        } else {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Unknown command. Usage: " + getCommandUsage(sender)));
        }
    }

    private void processStatsCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bwm stats <username> [mode]"));
            return;
        }
        String username = args[0];
        if (args.length > 1) {
            String modeStr = args[1];
            try {
                BedwarsStatsConfig.BedwarsMode mode = BedwarsStatsConfig.BedwarsMode.fromString(modeStr);
                HypixelApiHandler.processPlayer(username, mode);
            } catch (IllegalArgumentException e) {
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid mode. Use tab-completion for suggestions."));
            }
        } else {
            HypixelApiHandler.processPlayer(username);
        }
    }

    private void processSettingsCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bwm settings <apikey|mode|nick|showrank|displayorder>"));
            return;
        }
        String setting = args[0];
        if (args.length < 2 && !setting.equalsIgnoreCase("help")) {
            sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Usage: /bwm settings <setting> <value>"));
            return;
        }
        String value = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        switch(setting.toLowerCase()) {
            case "apikey":
                BedwarsStatsConfig.setApiKey(value);
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "API Key set successfully!"));
                break;
            case "mode":
                try {
                    // Validate the mode exists before setting it
                    BedwarsStatsConfig.BedwarsMode mode = BedwarsStatsConfig.BedwarsMode.fromString(value);
                    BedwarsStatsConfig.setBedwarsMode(mode.name());
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Bedwars mode set to " + mode.getDisplayName()));
                } catch (IllegalArgumentException e) {
                    sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid mode."));
                }
                break;
            case "nick":
                BedwarsStatsConfig.setMyNick(value);
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Your nick has been set to: " + value));
                break;
            case "showrank":
                boolean showRank = Boolean.parseBoolean(value);
                BedwarsStatsConfig.setShowRankPrefix(showRank);
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Rank prefix display set to: " + showRank));
                break;
            case "displayorder":
                BedwarsStatsConfig.setDisplayOrder(value);
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Display order set to: " + value));
                break;
            default:
                sender.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "Unknown setting."));
                break;
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "stats", "settings");
        } else if (args.length > 1) {
            if ("stats".equalsIgnoreCase(args[0])) {
                if (args.length == 2) {
                    // Suggest online player names - this is complex, so we'll just return an empty list for now.
                    return new ArrayList<>();
                } else if (args.length == 3) {
                    List<String> suggestions = new ArrayList<>();
                    for (BedwarsStatsConfig.BedwarsMode mode : BedwarsStatsConfig.BedwarsMode.values()) {
                        suggestions.add(mode.name().toLowerCase());
                        suggestions.addAll(Arrays.asList(mode.getAliases()));
                    }
                    return getListOfStringsMatchingLastWord(args, suggestions);
                }
            } else if ("settings".equalsIgnoreCase(args[0])) {
                if (args.length == 2) {
                    return getListOfStringsMatchingLastWord(args, "apikey", "mode", "nick", "showrank", "displayorder");
                }
            }
        }
        return null;
    }
}
