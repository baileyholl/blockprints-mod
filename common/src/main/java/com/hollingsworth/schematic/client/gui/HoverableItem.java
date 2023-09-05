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
    public void getTooltip(List<Component> tooltip) {
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int x, int y, float v) {
        RenderUtils.drawItemAsIcon(stack, guiGraphics.pose(), x, y, 16, false);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
