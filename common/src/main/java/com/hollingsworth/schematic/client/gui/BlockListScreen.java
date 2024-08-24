package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class BlockListScreen extends BaseSchematicScreen {
    public Screen previousScreen;
    List<DownloadScreen.BlockListEntry> entries;
    List<BlockEntryRow> rows = new ArrayList<>();
    int scroll = 0;

    public BlockListScreen(Screen previousScreen, List<DownloadScreen.BlockListEntry> entries) {
        super();
        this.previousScreen = previousScreen;
        this.entries = entries;
        this.entries.sort((o1, o2) -> {
            if (o1.isMissing && !o2.isMissing) {
                return -1;
            } else if (!o1.isMissing && o2.isMissing) {
                return 1;
            } else {
                return Integer.compare(o2.count, o1.count);
            }
        });
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new GuiImageButton(bookLeft + 9, bookTop + 9, 15, 15, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_back.png"), b -> {
            Minecraft.getInstance().setScreen(previousScreen);
        }));
        updateList();
        int scrollSize = Math.max(0, entries.size() - 10);
        addRenderableWidget(new VerticalSlider(bookLeft + 265, bookTop + 46, scrollSize, 1, 1, count -> {
            this.scroll = count;
            updateList();
        }));
    }

    public void updateList() {
        for (BlockEntryRow row : rows) {
            removeWidget(row);
        }
        rows = new ArrayList<>();
        List<DownloadScreen.BlockListEntry> sliced = entries.subList(scroll, Math.min(scroll + 10, entries.size()));
        for (int i = 0; i < Math.min(sliced.size(), 10); i++) {
            var entry = sliced.get(i);
            BlockEntryRow row = new BlockEntryRow(bookLeft + 25, bookTop + 43 + (i * 14), entry);
            rows.add(row);
            addRenderableWidget(row);
        }
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container_list_blocks.png"), 25, 25, 0, 0, 239, 159, 239, 159);
        graphics.drawString(font, Component.translatable("blockprints.block"), 30, 29, 0, false);
        graphics.drawString(font, Component.translatable("blockprints.qty"), 235, 29, 0, false);
    }

}
