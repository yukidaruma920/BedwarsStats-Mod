package com.yuki920.bedwarsstats;

import net.minecraft.util.EnumChatFormatting;

public class PrestigeFormatter {

    public static String formatPrestige(int stars) {
        EnumChatFormatting color = getPrestigeColor(stars);
        String icon = getPrestigeIcon(stars);

        if (stars < 100) {
             return EnumChatFormatting.GRAY + "[" + stars + icon + "]";
        }

        return color + "[" + stars + icon + "]";
    }

    private static EnumChatFormatting getPrestigeColor(int stars) {
        if (stars >= 3000) return EnumChatFormatting.DARK_AQUA;
        if (stars >= 2000) return EnumChatFormatting.DARK_PURPLE;
        if (stars >= 1000) return EnumChatFormatting.RED;
        if (stars >= 900) return EnumChatFormatting.GOLD;
        if (stars >= 800) return EnumChatFormatting.LIGHT_PURPLE;
        if (stars >= 700) return EnumChatFormatting.DARK_BLUE;
        if (stars >= 600) return EnumChatFormatting.DARK_GREEN;
        if (stars >= 500) return EnumChatFormatting.DARK_AQUA;
        if (stars >= 400) return EnumChatFormatting.DARK_RED;
        if (stars >= 300) return EnumChatFormatting.YELLOW;
        if (stars >= 200) return EnumChatFormatting.AQUA;
        if (stars >= 100) return EnumChatFormatting.WHITE;
        return EnumChatFormatting.GRAY;
    }

    private static String getPrestigeIcon(int stars) {
        if (stars >= 3000) return "✫";
        if (stars >= 2000) return "✥";
        if (stars >= 1000) return "❤";
        if (stars >= 900) return "✪";
        return "⭐"; // Default star for levels 100-899 and below
    }
}