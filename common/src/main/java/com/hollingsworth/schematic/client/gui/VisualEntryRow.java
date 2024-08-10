package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.renderer.StructureRenderData;
import com.hollingsworth.schematic.client.renderer.StructureRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class VisualEntryRow extends NestedWidget{
    StructureRenderData data;
    ManageVisualScreen screen;
    public VisualEntryRow(int x, int y, StructureRenderData data, ManageVisualScreen visualScreen) {
        super(x, y, 236, 12, Component.empty());
        this.data = data;
        this.screen = visualScreen;
        renderables.add(new GuiImageButton(x + 224, y + 1, 11, 11, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_remove_favorite.png"), button -> {
            StructureRenderer.structures.remove(data);
            screen.updateList();
        }).withTooltip(Component.translatable("blockprints.remove_visual")));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int i, int i1, float v) {
        super.renderWidget(graphics, i, i1, v);
        graphics.drawString(Minecraft.getInstance().font, data.name, x + 16, y + 3, 0x000000, false);
    }
}
