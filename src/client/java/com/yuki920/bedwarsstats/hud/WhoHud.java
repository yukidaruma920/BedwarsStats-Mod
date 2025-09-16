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

        // --- Auto-sizing logic ---
        String title = "Bedwars Stats (/who)";
        int padding = 5;
        int titleMargin = 5;

        // Calculate width
        int maxWidth = textRenderer.getWidth(title);
        for (String line : lines) {
            maxWidth = Math.max(maxWidth, textRenderer.getWidth(line));
        }
        int contentWidth = maxWidth + (padding * 2);

        // Calculate height
        int contentHeight = (textRenderer.fontHeight + 2) * (lines.size() + 1) + (padding * 2) + titleMargin;

        // --- End auto-sizing logic ---

        int x = config.whoHud.hudX;
        int y = config.whoHud.hudY;
        float scale = config.whoHud.hudScalePercent / 100.0f;

        int finalWidth = (int) (contentWidth * scale);
        int finalHeight = (int) (contentHeight * scale);

        // Draw background
        drawContext.fill(x, y, x + finalWidth, y + finalHeight, 0x80000000); // Semi-transparent black

        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(x, y, 0);
        drawContext.getMatrices().scale(scale, scale, 1.0f);

        // Draw title
        int titleWidth = textRenderer.getWidth(title);
        drawContext.drawText(textRenderer, title, (contentWidth - titleWidth) / 2, padding, 0xFFFFFF, true);

        // Draw stats
        int lineY = padding + textRenderer.fontHeight + titleMargin;
        for (String line : lines) {
            drawContext.drawText(textRenderer, Text.literal(line), padding, lineY, 0xFFFFFF, false);
            lineY += (textRenderer.fontHeight + 2);
        }

        drawContext.getMatrices().pop();
    }
}
