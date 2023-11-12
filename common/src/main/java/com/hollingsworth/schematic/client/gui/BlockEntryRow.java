package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.RenderUtils;
import com.hollingsworth.schematic.common.util.ITooltipProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BlockEntryRow extends AbstractWidget implements ITooltipProvider {
    DownloadScreen.BlockListEntry entry;

    public BlockEntryRow(int x, int y, DownloadScreen.BlockListEntry entry) {
        super(x, y, 238, 15, Component.empty());
        this.entry = entry;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int i, int i1, float v) {
        if (!entry.isMissing) {
            PoseStack stack = guiGraphics.pose();
            RenderUtils.drawItemAsIcon(entry.renderStack, stack, x + 1, y - 0.3f, 10.0f);
        } else {
            guiGraphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_notice.png"), x + 6, y + 3, 0, 0, 6, 9, 6, 9);
        }
        guiGraphics.drawString(Minecraft.getInstance().font, entry.name, x + 17, y + 4, 0, false);
        MutableComponent component = Component.literal("" + entry.count);
        GuiUtils.drawCenteredStringNoShadow(Minecraft.getInstance().font, guiGraphics, component, x + 243 - 25, y + 4, 0);

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public void getTooltip(List<Component> tooltip) {
        if (entry.isMissing) {
            tooltip.add(Component.translatable("blockprints.block_missing", entry.name).withStyle(ChatFormatting.RED));
        }
    }
}
