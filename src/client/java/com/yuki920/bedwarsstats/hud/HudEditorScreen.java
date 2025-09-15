package com.yuki920.bedwarsstats.hud;

import com.yuki920.bedwarsstats.config.BedwarsStatsConfig;
import me.shedaniel.autoconfig.AutoConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class HudEditorScreen extends Screen {

    private final BedwarsStatsConfig config;
    private boolean isDragging = false;
    private boolean isResizing = false;
    private double dragStartX, dragStartY;
    private final int resizeHandleSize = 5;

    public HudEditorScreen() {
        super(Text.literal("HUD Editor"));
        this.config = AutoConfig.getConfigHolder(BedwarsStatsConfig.class).getConfig();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        // In the editor, we can render a dummy HUD to show where it is.
        int x = config.whoHud.hudX;
        int y = config.whoHud.hudY;
        int width = config.whoHud.hudWidth;
        int height = config.whoHud.hudHeight;

        // Draw a border to indicate the editable area
        context.drawBorder(x - 1, y - 1, width + 2, height + 2, 0xFFFFFFFF);
        context.fill(x, y, x + width, y + height, 0x80000000);
        context.drawText(this.textRenderer, "Drag to move", x + 5, y + 5, 0xFFFFFF, false);
        context.drawText(this.textRenderer, "Drag corner to resize", x + 5, y + 20, 0xFFFFFF, false);
        context.drawText(this.textRenderer, "Close to save", x + 5, y + 35, 0xFFFFFF, false);

        // Draw resize handle
        context.fill(x + width - resizeHandleSize, y + height - resizeHandleSize, x + width, y + height, 0xFFFFFFFF);
    }

    private boolean isMouseOverResizeHandle(double mouseX, double mouseY) {
        int x = config.whoHud.hudX;
        int y = config.whoHud.hudY;
        int width = config.whoHud.hudWidth;
        int height = config.whoHud.hudHeight;
        return mouseX >= x + width - resizeHandleSize && mouseX <= x + width &&
               mouseY >= y + height - resizeHandleSize && mouseY <= y + height;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int x = config.whoHud.hudX;
        int y = config.whoHud.hudY;
        int width = config.whoHud.hudWidth;
        int height = config.whoHud.hudHeight;

        if (button == 0) {
            if (isMouseOverResizeHandle(mouseX, mouseY)) {
                this.isResizing = true;
                return true;
            }
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
                this.isDragging = true;
                this.dragStartX = mouseX - x;
                this.dragStartY = mouseY - y;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (this.isResizing) {
            int newWidth = (int) (mouseX - config.whoHud.hudX);
            int newHeight = (int) (mouseY - config.whoHud.hudY);
            config.whoHud.hudWidth = Math.max(50, newWidth); // Set a minimum size
            config.whoHud.hudHeight = Math.max(30, newHeight);
            return true;
        }
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
            this.isResizing = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        AutoConfig.getConfigHolder(BedwarsStatsConfig.class).save();
        super.close();
    }
}
