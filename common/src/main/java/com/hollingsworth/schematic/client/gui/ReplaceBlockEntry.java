package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.RenderUtils;
import com.hollingsworth.schematic.common.util.ITooltipProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class ReplaceBlockEntry extends NestedWidget implements ITooltipProvider{
    Block renderBlock;
    Button.OnPress onPress;

    public ReplaceBlockEntry(int x, int y, Block block, Button.OnPress onPress) {
        super(x, y, 238, 15, Component.empty());
        this.renderBlock = block;
        this.onPress = onPress;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        PoseStack stack = guiGraphics.pose();
        RenderUtils.drawItemAsIcon(renderBlock.asItem().getDefaultInstance(), stack, x + 1, y - 0.3f, 10.0f);

        guiGraphics.drawString(Minecraft.getInstance().font, renderBlock.getName().getString(), x + 17, y + 4, 0, false);
    }

    @Override
    public List<AbstractWidget> getExtras() {
        GuiImageButton button = new GuiImageButton(x + 185, y, 15, 15,  ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_delete.png"), b -> onPress.onPress(b));
        return List.of(button);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void getTooltip(List<Component> tooltip) {
    }
}
