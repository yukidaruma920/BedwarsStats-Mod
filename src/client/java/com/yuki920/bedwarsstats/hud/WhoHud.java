package com.yuki920.bedwarsstats.hud;

import com.yuki920.bedwarsstats.config.BedwarsStatsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;

import java.util.List;

public class WhoHud implements HudRenderCallback {
    @Override
    public void onHudRender(DrawContext drawContext, RenderTickCounter tickCounter) {
        BedwarsStatsConfig config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
        if (!config.whoHud.showWhoHud) {
            return;
        }

        MinecraftClient client = MinecraftClient.getInstance();
        TextRenderer textRenderer = client.textRenderer;
        List<String> lines = HudData.getInstance().getLines();

        if (lines.isEmpty()) {
            return;
        }

        int x = config.whoHud.hudX;
        int y = config.whoHud.hudY;
        int width = config.whoHud.hudWidth;
        int height = config.whoHud.hudHeight;

        // Draw background
        drawContext.fill(x, y, x + width, y + height, 0x80000000); // Semi-transparent black

        // Draw title
        String title = "Bedwars Stats (/who)";
        int titleWidth = textRenderer.getWidth(title);
        drawContext.drawText(textRenderer, title, x + (width - titleWidth) / 2, y + 5, 0xFFFFFF, true);

        // Draw stats
        int lineY = y + 20;
        for (String line : lines) {
            if (lineY > y + height - 10) {
                // Stop drawing if it overflows the HUD area
                break;
            }
            drawContext.drawText(textRenderer, Text.literal(line), x + 5, lineY, 0xFFFFFF, false);
            lineY += 10;
        }
    }
}
