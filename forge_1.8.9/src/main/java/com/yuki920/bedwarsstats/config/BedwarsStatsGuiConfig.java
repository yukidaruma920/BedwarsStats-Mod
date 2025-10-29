package com.yuki920.bedwarsstats.config;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class BedwarsStatsGuiConfig extends GuiScreen {

    private GuiScreen parentScreen;
    private GuiTextField apiKeyField;
    private GuiTextField nickField;
    private GuiTextField displayOrderField;
    private GuiButton modeButton;
    private GuiButton showRankButton;

    private BedwarsStatsConfig.BedwarsMode currentMode;
    private boolean showRank;

    public BedwarsStatsGuiConfig(GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();

        int y = this.height / 4 - 16;

        this.apiKeyField = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, y, 200, 20);
        this.apiKeyField.setMaxStringLength(36);
        this.apiKeyField.setText(BedwarsStatsConfig.getApiKey());

        y += 24;
        this.nickField = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 100, y, 200, 20);
        this.nickField.setMaxStringLength(16);
        this.nickField.setText(BedwarsStatsConfig.getMyNick());

        y += 24;
        this.displayOrderField = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, y, 200, 20);
        this.displayOrderField.setMaxStringLength(100);
        this.displayOrderField.setText(BedwarsStatsConfig.getDisplayOrder());

        y += 24;
        this.currentMode = BedwarsStatsConfig.BedwarsMode.fromString(BedwarsStatsConfig.getBedwarsMode());
        this.modeButton = new GuiButton(3, this.width / 2 - 100, y, 200, 20, "Mode: " + currentMode.getDisplayName());
        this.buttonList.add(this.modeButton);

        y += 24;
        this.showRank = BedwarsStatsConfig.getShowRankPrefix();
        this.showRankButton = new GuiButton(4, this.width / 2 - 100, y, 200, 20, "Show Rank: " + getFormattedBoolean(showRank));
        this.buttonList.add(this.showRankButton);

        this.buttonList.add(new GuiButton(100, this.width / 2 - 100, this.height - 29, 200, 20, "Done"));
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        BedwarsStatsConfig.setApiKey(this.apiKeyField.getText());
        BedwarsStatsConfig.setMyNick(this.nickField.getText());
        BedwarsStatsConfig.setDisplayOrder(this.displayOrderField.getText());
        BedwarsStatsConfig.setBedwarsMode(this.currentMode.name());
        BedwarsStatsConfig.setShowRankPrefix(this.showRank);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.enabled) {
            if (button.id == 3) { // Mode
                int next = (currentMode.ordinal() + 1) % BedwarsStatsConfig.BedwarsMode.values().length;
                currentMode = BedwarsStatsConfig.BedwarsMode.values()[next];
                button.displayString = "Mode: " + currentMode.getDisplayName();
            }
            if (button.id == 4) { // Show Rank
                showRank = !showRank;
                button.displayString = "Show Rank: " + getFormattedBoolean(showRank);
            }
            if (button.id == 100) { // Done
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.apiKeyField.textboxKeyTyped(typedChar, keyCode);
        this.nickField.textboxKeyTyped(typedChar, keyCode);
        this.displayOrderField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.apiKeyField.mouseClicked(mouseX, mouseY, mouseButton);
        this.nickField.mouseClicked(mouseX, mouseY, mouseButton);
        this.displayOrderField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Bedwars Stats Settings", this.width / 2, 20, 16777215);

        this.drawString(this.fontRendererObj, "Hypixel API Key", this.width / 2 - 100, this.apiKeyField.yPosition - 12, 10526880);
        this.apiKeyField.drawTextBox();

        this.drawString(this.fontRendererObj, "Your Nick (if nicked)", this.width / 2 - 100, this.nickField.yPosition - 12, 10526880);
        this.nickField.drawTextBox();

        this.drawString(this.fontRendererObj, "Display Order", this.width / 2 - 100, this.displayOrderField.yPosition - 12, 10526880);
        this.displayOrderField.drawTextBox();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    private String getFormattedBoolean(boolean value) {
        return value ? EnumChatFormatting.GREEN + "ON" : EnumChatFormatting.RED + "OFF";
    }
}
