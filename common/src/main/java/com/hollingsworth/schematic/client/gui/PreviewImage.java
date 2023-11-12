package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.export.LytSize;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.hollingsworth.schematic.client.gui.ScenePreview.getScaledDimension;

public class PreviewImage extends AbstractWidget {
    DynamicTexture dynamicTexture;
    ResourceLocation resourceLocation;

    public PreviewImage(int x, int y, int width, int height, DynamicTexture dynamicTexture, ResourceLocation resourceLocation) {
        super(x, y, width, height, Component.empty());
        this.dynamicTexture = dynamicTexture;
        this.resourceLocation = resourceLocation;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        int imageWidth = dynamicTexture.getPixels().getWidth();
        int imageHeight = dynamicTexture.getPixels().getHeight();

        // scale width and height to fit in the box of 100,100
        LytSize origDim = new LytSize(imageWidth, imageHeight);
        LytSize boundary = new LytSize(100, 100);
        LytSize newDim = getScaledDimension(origDim, boundary);
        // Offset x and Y so the image is centered
        // center x and y on point 50, 50
        int x = this.x + 143 / 2;
        int y = this.y + 111 / 2;
        x -= newDim.width() / 2;
        y -= newDim.height() / 2;
        guiGraphics.blit(resourceLocation, x, y, 0, 0, newDim.width(), newDim.height(), newDim.width(), newDim.height());
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void playDownSound(SoundManager $$0) {

    }
}
