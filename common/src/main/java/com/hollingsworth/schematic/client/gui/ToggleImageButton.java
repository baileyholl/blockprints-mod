package com.hollingsworth.schematic.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public class ToggleImageButton extends GuiImageButton {
    public Supplier<Boolean> selected;
    public ResourceLocation selectedImage;
    public ResourceLocation unselectedImage;

    public ToggleImageButton(int x, int y, int w, int h, ResourceLocation unSelectedImage, ResourceLocation selectedImage, OnPress onPress, Supplier<Boolean> isSelected) {
        super(x, y, w, h, unSelectedImage, onPress);
        this.selected = isSelected;
        this.unselectedImage = unSelectedImage;
        this.selectedImage = selectedImage;
    }

    @Override
    public void onPress() {
        super.onPress();

    }

    @Override
    public void render(GuiGraphics graphics, int parX, int parY, float partialTicks) {
        this.image = this.selected.get() ? this.selectedImage : this.unselectedImage;
        super.render(graphics, parX, parY, partialTicks);
    }
}
