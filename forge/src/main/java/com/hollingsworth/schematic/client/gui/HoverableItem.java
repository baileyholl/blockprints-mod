package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.client.RenderUtils;
import com.hollingsworth.schematic.common.util.ITooltipProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class HoverableItem extends AbstractWidget implements ITooltipProvider {
    public ItemStack stack;

    public HoverableItem(int pX, int pY, ItemStack stack) {
        super(pX, pY, 16, 16, Component.empty());
        this.stack = stack;
    }

    @Override
    public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        if(!this.visible) return;
        RenderUtils.drawItemAsIcon(stack, pPoseStack, x, y, 16, false);
    }

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {

    }

    @Override
    public void getTooltip(List<Component> tooltip) {
    }
}
