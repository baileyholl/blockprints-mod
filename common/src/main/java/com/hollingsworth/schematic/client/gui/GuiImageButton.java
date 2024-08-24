package com.hollingsworth.schematic.client.gui;


import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.common.util.ITooltipProvider;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GuiImageButton extends ANButton implements ITooltipProvider {

    public ResourceLocation image;
    public int u, v, image_width, image_height;
    public List<Component> toolTip = new ArrayList<>();
    public boolean soundDisabled = false;

    public GuiImageButton(int x, int y, int w, int h, ResourceLocation image, OnPress onPress) {
        this(x, y, 0, 0, w, h, w, h, image, onPress);
    }

    public GuiImageButton(int x, int y, int u, int v, int w, int h, int image_width, int image_height, String resource_image, OnPress onPress) {
        this(x, y, u, v, w, h, image_width, image_height, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, resource_image), onPress);
    }

    public GuiImageButton(int x, int y, int u, int v, int w, int h, int image_width, int image_height, ResourceLocation image, OnPress onPress) {
        super(x, y, w, h, Component.literal(""), onPress);
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
        this.image_height = image_height;
        this.image_width = image_width;
        this.image = image;
    }

    public GuiImageButton withTooltip(@Nullable Component toolTip) {
        if (toolTip == null)
            return this;
        this.toolTip.add(toolTip);
        return this;
    }

    public GuiImageButton withTooltip(@NotNull List<Component> tooltip) {
        this.toolTip = tooltip;
        return this;
    }

    @Override
    protected void renderWidget(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (visible) {
            pGuiGraphics.blit(image, x, y, u, v, width, height, image_width, image_height);
        }
    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (toolTip != null)
            tooltip.addAll(toolTip);
    }

    @Override
    public void playDownSound(SoundManager pHandler) {
        if (soundDisabled)
            return;
        super.playDownSound(pHandler);
    }

    public void setPosition(int pX, int pY) {
        this.x = pX;
        this.y = pY;
    }
}