package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class ReplaceEntryScreen extends BaseSchematicScreen{

    public BaseSchematicScreen previousScreen;
    public Block replacingBlock;
    List<Block> allBlocks = new ArrayList<>();
    List<AbstractWidget> rows = new ArrayList<>();
    int scroll = 0;
    VerticalSlider slider;
    Consumer<Block> onBlockSelected;

    public ReplaceEntryScreen(BaseSchematicScreen previousScreen, Block replaceBlock, Consumer<Block> onBlockSelected) {
        super();
        this.previousScreen = previousScreen;
        replacingBlock = replaceBlock;
        allBlocks = new ArrayList<>(Minecraft.getInstance().level.registryAccess().registryOrThrow(Registries.BLOCK).stream().toList());
        allBlocks.sort(Comparator.comparing(a -> a.getName().getString()));
        this.onBlockSelected = onBlockSelected;
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new GuiImageButton(bookLeft + 9, bookTop + 9, 15, 15, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/button_back.png"), b -> {
            Minecraft.getInstance().setScreen(previousScreen);
        }));
        updateList();
        int maxScroll = Math.max(0, allBlocks.size() - 10);
        slider = addRenderableWidget(new VerticalSlider(bookLeft + 265, bookTop + 46, maxScroll, 1, 1, this::scrollChange));
    }

    public void scrollChange(int change) {
        this.scroll = change;
        updateList();
    }

    @Override
    public boolean mouseScrolled(double pMouseX, double pMouseY, double pScrollX, double pScrollY) {
        if(slider.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY)){
            return true;
        }
        return super.mouseScrolled(pMouseX, pMouseY, pScrollX, pScrollY);
    }


    public void updateList() {
        for (AbstractWidget row : rows) {
            removeWidget(row);
        }
        rows = new ArrayList<>();
        List<Block> sliced = allBlocks.subList(scroll, Math.min(scroll + 10, allBlocks.size()));
        for (int i = 0; i < Math.min(sliced.size(), 10); i++) {
            var entry = sliced.get(i);
            ReplaceBlockEntry row = new ReplaceBlockEntry(bookLeft + 25, bookTop + 43 + (i * 14), entry, (b) ->{
                onBlockSelected.accept(entry);
            });
            rows.add(row);
            addRenderableWidget(row);
        }
    }


    @Override
    public void render(GuiGraphics matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);

    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/container_list_blocks.png"), 25, 25, 0, 0, 239, 159, 239, 159);
        graphics.drawString(font, Component.translatable("blockprints.replacing", replacingBlock.getName().getString()), 30, 29, 0, false);
//        graphics.drawString(font, Component.translatable("blockprints.qty"), 235, 29, 0, false);
    }
}
