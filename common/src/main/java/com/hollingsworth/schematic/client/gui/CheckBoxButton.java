package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class CheckBoxButton extends GuiImageButton{
    Supplier<Boolean> selected;
    public CheckBoxButton(int x, int y, OnPress onPress, Supplier<Boolean> selected) {
        super(x, y, 95, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/" + "container_6.png"), onPress);
        this.selected = selected;
    }

    @Override
    public void render(GuiGraphics graphics, int parX, int parY, float partialTicks) {
        super.render(graphics, parX, parY, partialTicks);
        if (visible) {
            ResourceLocation unchecked = new ResourceLocation(Constants.MOD_ID, "textures/gui/container_filter_unchecked.png");
            ResourceLocation checked = new ResourceLocation(Constants.MOD_ID, "textures/gui/container_filter_checked.png");
            graphics.blit(selected.get() ? checked : unchecked, x + 4, y + 4, 0, 0, 7, 7, 7, 7);
        }
    }
}
