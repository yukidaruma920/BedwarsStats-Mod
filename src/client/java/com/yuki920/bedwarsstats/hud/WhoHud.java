package com.yuki920.bedwarsstats.hud;

import com.yuki920.bedwarsstats.config.BedwarsStatsConfig;
import com.yuki920.bedwarsstats.stats.PlayerStats;
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
        List<PlayerStats> statsList = HudData.getInstance().getPlayerStats();

        if (statsList.isEmpty()) {
            return;
        }

        // --- Table Layout Logic ---
        int padding = 5;
        int columnSpacing = 10;
        boolean textShadow = config.whoHud.textShadow;

        // 1. Define Headers
        String[] headers = {"Player", "Wins", "WLR", "Finals", "FKDR"};

        // 2. Calculate Column Widths
        int[] colWidths = new int[headers.length];
        colWidths[0] = textRenderer.getWidth(headers[0]);
        colWidths[1] = textRenderer.getWidth(headers[1]);
        colWidths[2] = textRenderer.getWidth(headers[2]);
        colWidths[3] = textRenderer.getWidth(headers[3]);
        colWidths[4] = textRenderer.getWidth(headers[4]);

        for (PlayerStats stats : statsList) {
            colWidths[0] = Math.max(colWidths[0], textRenderer.getWidth(stats.star() + " " + stats.rank() + stats.username()));
            colWidths[1] = Math.max(colWidths[1], textRenderer.getWidth(stats.wins()));
            colWidths[2] = Math.max(colWidths[2], textRenderer.getWidth(stats.wlr()));
            colWidths[3] = Math.max(colWidths[3], textRenderer.getWidth(stats.finals()));
            colWidths[4] = Math.max(colWidths[4], textRenderer.getWidth(stats.fkdr()));
        }

        // 3. Calculate Total Size
        int contentWidth = (padding * 2);
        for (int width : colWidths) {
            contentWidth += width;
        }
        contentWidth += columnSpacing * (headers.length - 1);
        int contentHeight = (textRenderer.fontHeight + 2) * (statsList.size() + 1) + (padding * 2);

        // 4. Apply Scaling and Position
        int x = config.whoHud.hudX;
        int y = config.whoHud.hudY;
        float scale = config.whoHud.hudScalePercent / 100.0f;
        int finalWidth = (int) (contentWidth * scale);
        int finalHeight = (int) (contentHeight * scale);

        // 5. Render Background
        drawContext.fill(x, y, x + finalWidth, y + finalHeight, 0x80000000);

        // 6. Setup for Rendering Text
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(x, y, 0);
        drawContext.getMatrices().scale(scale, scale, 1.0f);

        int currentX = padding;
        int currentY = padding;

        // 7. Render Header
        for (int i = 0; i < headers.length; i++) {
            // Right-align headers for numeric columns
            int textX = (i > 0) ? currentX + colWidths[i] - textRenderer.getWidth(headers[i]) : currentX;
            drawContext.drawText(textRenderer, headers[i], textX, currentY, 0xFFFFFF, textShadow);
            currentX += colWidths[i] + columnSpacing;
        }
        currentY += textRenderer.fontHeight + 2;

        // 8. Render Rows
        for (PlayerStats stats : statsList) {
            currentX = padding;
            // Player Column (Left-aligned)
            drawContext.drawText(textRenderer, Text.literal(stats.star() + " " + stats.rank() + stats.username()), currentX, currentY, 0xFFFFFF, textShadow);
            currentX += colWidths[0] + columnSpacing;
            // Wins Column (Right-aligned)
            drawContext.drawText(textRenderer, Text.literal(stats.winsColor() + stats.wins()), currentX + colWidths[1] - textRenderer.getWidth(stats.wins()), currentY, 0xFFFFFF, textShadow);
            currentX += colWidths[1] + columnSpacing;
            // WLR Column (Right-aligned)
            drawContext.drawText(textRenderer, Text.literal(stats.wlrColor() + stats.wlr()), currentX + colWidths[2] - textRenderer.getWidth(stats.wlr()), currentY, 0xFFFFFF, textShadow);
            currentX += colWidths[2] + columnSpacing;
            // Finals Column (Right-aligned)
            drawContext.drawText(textRenderer, Text.literal(stats.finalsColor() + stats.finals()), currentX + colWidths[3] - textRenderer.getWidth(stats.finals()), currentY, 0xFFFFFF, textShadow);
            currentX += colWidths[3] + columnSpacing;
            // FKDR Column (Right-aligned)
            drawContext.drawText(textRenderer, Text.literal(stats.fkdrColor() + stats.fkdr()), currentX + colWidths[4] - textRenderer.getWidth(stats.fkdr()), currentY, 0xFFFFFF, textShadow);

            currentY += textRenderer.fontHeight + 2;
        }

        drawContext.getMatrices().pop();
    }
}
