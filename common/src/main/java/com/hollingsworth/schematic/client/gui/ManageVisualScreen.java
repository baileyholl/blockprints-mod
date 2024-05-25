package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.renderer.StructureRenderData;
import com.hollingsworth.schematic.client.renderer.StructureRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ManageVisualScreen extends BaseSchematicScreen{
    List<VisualEntryRow> rows = new ArrayList<>();
    ArrayList<StructureRenderData> structures;
    int scroll = 0;
    public ManageVisualScreen() {
        super();
        structures = StructureRenderer.structures;
    }

    @Override
    public void init() {
        super.init();
        updateList();
        int scrollSize = Math.max(0, structures.size() - 10);
        addRenderableWidget(new VerticalSlider(bookLeft + 265, bookTop + 46, scrollSize, 1, 1, count -> {
            this.scroll = count;
            updateList();
        }));
        addRenderableWidget(new GuiImageButton(bookLeft + 9, bookTop + 9, 15, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_back.png"), b -> {
            Minecraft.getInstance().setScreen(new HomeScreen());
        }));
    }

    public void updateList() {
        for (VisualEntryRow row : rows) {
            removeWidget(row);
        }
        rows = new ArrayList<>();
        List<StructureRenderData> sliced = structures.subList(scroll, Math.min(scroll + 10, structures.size()));
        for (int i = 0; i < Math.min(sliced.size(), 10); i++) {
            var entry = sliced.get(i);
            VisualEntryRow row = new VisualEntryRow(bookLeft + 26, bookTop + 44 + (i * 14), entry, this);
            rows.add(row);
            addRenderableWidget(row);
        }
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/container_list_visualizer.png"), 25, 25, 0, 0, 239, 159, 239, 159);
        GuiUtils.drawOutlinedText(font, graphics, Component.translatable("blockprints.visualizations").getVisualOrderText(), 30, 29);
    }
}
