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
        // Calculate a dummy size for the preview based on a table layout
        if (this.textRenderer == null) {
            this.dummyWidth = 150;
            this.dummyHeight = 100;
            return;
        }

        int padding = 5;
        int columnSpacing = 10;
        String[] headers = {"Player", "Wins", "WLR", "Finals", "FKDR"};
        int[] colWidths = new int[headers.length];

        // Sample data to estimate widths
        String samplePlayer = "[100✫] [MVP++] yuki920";
        String sampleWins = "1,234";
        String sampleWlr = "12.34";
        String sampleFinals = "5,678";
        String sampleFkdr = "56.78";

        colWidths[0] = Math.max(textRenderer.getWidth(headers[0]), textRenderer.getWidth(samplePlayer));
        colWidths[1] = Math.max(textRenderer.getWidth(headers[1]), textRenderer.getWidth(sampleWins));
        colWidths[2] = Math.max(textRenderer.getWidth(headers[2]), textRenderer.getWidth(sampleWlr));
        colWidths[3] = Math.max(textRenderer.getWidth(headers[3]), textRenderer.getWidth(sampleFinals));
        colWidths[4] = Math.max(textRenderer.getWidth(headers[4]), textRenderer.getWidth(sampleFkdr));

        int contentWidth = (padding * 2);
        for (int width : colWidths) {
            contentWidth += width;
        }
        contentWidth += columnSpacing * (headers.length - 1);
        // Estimate height for 3 rows + header
        int contentHeight = (this.textRenderer.fontHeight + 2) * (3 + 1) + (padding * 2);

        this.dummyWidth = contentWidth;
        this.dummyHeight = contentHeight;
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
