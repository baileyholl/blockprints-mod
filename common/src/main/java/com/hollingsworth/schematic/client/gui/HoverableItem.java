package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.client.RenderUtils;
import com.hollingsworth.schematic.common.util.ITooltipProvider;
import net.minecraft.client.gui.GuiGraphics;
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
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        if(!this.visible) return;
        RenderUtils.drawItemAsIcon(stack, guiGraphics.pose(), this.getX(), this.getY(), 16, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void getTooltip(List<Component> tooltip) {
    }
}
