package com.yuki920.bedwarsstats.hud;

import com.yuki920.bedwarsstats.config.BedwarsStatsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;

public class HudEditorScreen extends Screen {

    private final BedwarsStatsConfig config;
    private boolean isDragging = false;
    private double dragStartX, dragStartY;

    private int dummyWidth = 0;
    private int dummyHeight = 0;

    public HudEditorScreen() {
        super(Text.literal("HUD Editor"));
        this.config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
    }

    @Override
    protected void init() {
        super.init();
        // Calculate a dummy size for the preview based on some sample data
        List<String> sampleLines = Arrays.asList("Player1 [100✫] FKDR: 2.0", "Player2 [200✫] FKDR: 3.0", "Player3 [300✫] FKDR: 4.0");
        String title = "Bedwars Stats (/who)";
        int padding = 5;
        int titleMargin = 5;

        int maxWidth = this.textRenderer.getWidth(title);
        for (String line : sampleLines) {
            maxWidth = Math.max(maxWidth, this.textRenderer.getWidth(line));
        }
        this.dummyWidth = maxWidth + (padding * 2);
        this.dummyHeight = (this.textRenderer.fontHeight + 2) * (sampleLines.size() + 1) + (padding * 2) + titleMargin;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        int x = config.whoHud.hudX;
        int y = config.whoHud.hudY;
        float scale = config.whoHud.hudScalePercent / 100.0f;

        int finalWidth = (int) (this.dummyWidth * scale);
        int finalHeight = (int) (this.dummyHeight * scale);

        context.fill(x, y, x + finalWidth, y + finalHeight, 0x80444444);
        context.drawBorder(x - 1, y - 1, finalWidth + 2, finalHeight + 2, 0xFFFFFFFF);
        context.drawText(this.textRenderer, "Drag to move HUD", x + 5, y + 5, 0xFFFFFF, false);
        context.drawText(this.textRenderer, "Close (Esc) to save", x + 5, y + 20, 0xFFFFFF, false);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        float scale = config.whoHud.hudScalePercent / 100.0f;
        int finalWidth = (int) (this.dummyWidth * scale);
        int finalHeight = (int) (this.dummyHeight * scale);
        int x = config.whoHud.hudX;
        int y = config.whoHud.hudY;

        if (button == 0 && mouseX >= x && mouseX <= x + finalWidth && mouseY >= y && mouseY <= y + finalHeight) {
            this.isDragging = true;
            this.dragStartX = mouseX - x;
            this.dragStartY = mouseY - y;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isDragging) {
            config.whoHud.hudX = (int) (mouseX - this.dragStartX);
            config.whoHud.hudY = (int) (mouseY - this.dragStartY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            this.isDragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        AutoConfig.getConfigHolder(BedwarsStatsConfig.class).save();
        super.close();
    }
}
