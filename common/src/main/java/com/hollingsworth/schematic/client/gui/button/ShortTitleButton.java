package com.hollingsworth.schematic.client.gui.button;

import com.hollingsworth.nuggets.client.gui.GuiHelpers;
import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.gui.GuiImageButton;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ShortTitleButton extends GuiImageButton {
    ResourceLocation icon;
    Component title;
    public ShortTitleButton(int x, int y, Component title, Component tooltip, ResourceLocation icon, OnPress onPress) {
        super(x, y, 79, 15, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_5.png"), onPress);
        withTooltip(tooltip);
        this.icon = icon;
        this.title = title;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.renderWidget(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        Font font = Minecraft.getInstance().font;
        pGuiGraphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/icon_visualize.png"), x + 3, y + 4, 0, 0, 9, 7, 9, 7);
        GuiHelpers.drawCenteredOutlinedText(font, pGuiGraphics, title.getVisualOrderText(), x + 45, y + 4);
    }
}
