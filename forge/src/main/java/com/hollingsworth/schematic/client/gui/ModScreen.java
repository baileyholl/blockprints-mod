package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.common.util.ITooltipProvider;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public abstract class ModScreen extends Screen {
    public int maxScale;
    public float scaleFactor;
    GuiGraphics graphics;
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
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        graphics = new GuiGraphics(matrixStack);
        matrixStack.pushPose();
        if (scaleFactor != 1) {
            matrixStack.scale(scaleFactor, scaleFactor, scaleFactor);
            mouseX /= scaleFactor;
            mouseY /= scaleFactor;
        }
        drawScreenAfterScale(graphics, mouseX, mouseY, partialTicks);
        matrixStack.popPose();
    }

    public void drawTooltip(GuiGraphics stack, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        collectTooltips(stack, mouseX, mouseY, tooltip);
        if (!tooltip.isEmpty()) {
//            stack.renderComponentTooltip(font, tooltip, mouseX, mouseY);
            renderComponentTooltip(stack.poseStack, tooltip, mouseX, mouseY);
        }
    }

    public void collectTooltips(GuiGraphics stack, int mouseX, int mouseY, List<Component> tooltip) {
        for (Widget renderable : renderables) {
            if (renderable instanceof AbstractWidget widget && renderable instanceof ITooltipProvider tooltipProvider) {
                if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, widget)) {
                    tooltipProvider.getTooltip(tooltip);
                    break;
                }
            }
        }
    }

    public @Nullable Widget getHoveredRenderable(int mouseX, int mouseY) {
        for (Widget renderable : renderables) {
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

    public void drawForegroundElements(int mouseX, int mouseY, float partialTicks) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    public void drawScreenAfterScale(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics.poseStack);
        PoseStack poseStack = graphics.poseStack;
        poseStack.pushPose();
        poseStack.translate(bookLeft, bookTop, 0);
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        drawForegroundElements(mouseX, mouseY, partialTicks);
        poseStack.popPose();
        super.render(graphics.poseStack, mouseX, mouseY, partialTicks);
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
}
