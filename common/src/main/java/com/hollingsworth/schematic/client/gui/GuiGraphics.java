package com.hollingsworth.schematic.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

// Throwaway class to keep compat with 1.20 port
public class GuiGraphics {
    public PoseStack poseStack;
    public GuiGraphics(PoseStack poseStack) {
        this.poseStack = poseStack;
    }
    public void blit(ResourceLocation pAtlasLocation, int pX, int pY, float pUOffset, float pVOffset, int pWidth, int pHeight, int pTextureWidth, int pTextureHeight) {
        RenderSystem.setShaderTexture(0, pAtlasLocation);
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
//        GuiComponent.blit(poseStack, pX, pY, pWidth, pHeight, pUOffset, pVOffset, pWidth, pHeight, pTextureWidth, pTextureHeight);
    }

    public void drawString(Font pFont, @Nullable String pText, int pX, int pY, int pColor, boolean pDropShadow) {
//        pFont.draw(poseStack, pText, pX, pY, pColor);
    }
}
