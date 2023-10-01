package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.common.util.ITooltipProvider;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ModScreen extends Screen {
    public int maxScale;
    public float scaleFactor;
    public int FULL_WIDTH;
    public int FULL_HEIGHT;
    public int bookLeft;
    public int bookTop;
    public int bookRight;
    public int bookBottom;

    public ModScreen(int width, int height) {
        super(Component.empty());
        this.FULL_HEIGHT = height;
        this.FULL_WIDTH = width;
    }

    @Override
    public void init() {
        super.init();
        this.maxScale = this.getMaxAllowedScale();
        this.scaleFactor = 1.0F;
        bookLeft = width / 2 - FULL_WIDTH / 2;
        bookTop = height / 2 - FULL_HEIGHT / 2;
        bookRight = width / 2 + FULL_WIDTH / 2;
        bookBottom = height / 2 + FULL_HEIGHT / 2;
    }

    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        PoseStack poseStack = matrixStack.pose();
        poseStack.pushPose();
        if (scaleFactor != 1) {
            poseStack.scale(scaleFactor, scaleFactor, scaleFactor);
            mouseX /= scaleFactor;
            mouseY /= scaleFactor;
        }
        drawScreenAfterScale(matrixStack, mouseX, mouseY, partialTicks);
        poseStack.popPose();
    }

    public void drawTooltip(GuiGraphics stack, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        collectTooltips(stack, mouseX, mouseY, tooltip);
        if (!tooltip.isEmpty()) {
            stack.renderComponentTooltip(font, tooltip, mouseX, mouseY);
        }
    }

    public void collectTooltips(GuiGraphics stack, int mouseX, int mouseY, List<Component> tooltip) {
        for (Renderable renderable : renderables) {
            if (renderable instanceof AbstractWidget widget && renderable instanceof ITooltipProvider tooltipProvider) {
                if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, widget)) {
                    tooltipProvider.getTooltip(tooltip);
                    break;
                }
            }
        }
    }

    public @Nullable Renderable getHoveredRenderable(int mouseX, int mouseY) {
        for (Renderable renderable : renderables) {
            if (renderable instanceof AbstractWidget widget) {
                if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, widget)) {
                    return renderable;
                }
            }
        }
        return null;
    }

    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(getBgTexture(), 0, 0, 0, 0, FULL_WIDTH, FULL_HEIGHT, FULL_WIDTH, FULL_HEIGHT);
    }

    public void drawForegroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void drawScreenAfterScale(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics);
        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.translate(bookLeft, bookTop, 0);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        drawForegroundElements(graphics, mouseX, mouseY, partialTicks);
        poseStack.popPose();
        super.render(graphics, mouseX, mouseY, partialTicks);
        drawTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int getMaxAllowedScale() {
        return this.minecraft.getWindow().calculateScale(0, this.minecraft.isEnforceUnicode());
    }

    public abstract ResourceLocation getBgTexture();


    public static void blitRect(PoseStack matrixStack, float x0, float y0, float xt, float yt, float width, float height, int tWidth, int tHeight, ResourceLocation texture, int alpha)
    {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha / 255.0f);
        RenderSystem.setShaderTexture(0, texture);

        float tx0 = xt / tWidth;
        float ty0 = yt / tHeight;
        float tx1 = tx0 + width / tWidth;
        float ty1 = ty0 + height / tHeight;

        float x1 = x0 + width;
        float y1 = y0 + height;

        Tesselator tess = Tesselator.getInstance();
        BufferBuilder builder = tess.getBuilder();
        builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        Matrix4f matrix = matrixStack.last().pose();
        builder.vertex(matrix, x0, y1, 0.0f).uv(tx0, ty1).endVertex();
        builder.vertex(matrix, x1, y1, 0.0f).uv(tx1, ty1).endVertex();
        builder.vertex(matrix, x1, y0, 0.0f).uv(tx1, ty0).endVertex();
        builder.vertex(matrix, x0, y0, 0.0f).uv(tx0, ty0).endVertex();
        tess.end();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1f);
    }
}
