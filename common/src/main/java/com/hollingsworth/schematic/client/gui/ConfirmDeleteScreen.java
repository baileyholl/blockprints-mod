package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.BlockprintsApi;
import com.hollingsworth.schematic.api.blockprints.download.PreviewDownloadResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class ConfirmDeleteScreen extends BaseSchematicScreen{
    public Screen screenOnYes;
    public Screen screenOnNo;
    public PreviewDownloadResult previewDownloadResult;

    public ConfirmDeleteScreen(PreviewDownloadResult previewDownloadResult, Screen screenOnYes, Screen screenOnNo) {
        super();
        this.screenOnYes = screenOnYes;
        this.screenOnNo = screenOnNo;
        this.previewDownloadResult = previewDownloadResult;
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new GuiImageButton(bookLeft + 9, bookTop + 9, 15, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_back.png"), b -> {
            Minecraft.getInstance().setScreen(screenOnNo);
        }));
        addRenderableWidget(new GuiImageButton(bookLeft + 105, bookTop + 137, 95, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_6_red.png"), b -> {
            Minecraft.getInstance().setScreen(screenOnYes);
            Minecraft.getInstance().setScreen(new LoadingScreen<>(() -> BlockprintsApi.getInstance().upload().postDelete(previewDownloadResult.downloadResponse.id), (success) -> {
                Minecraft.getInstance().setScreen(screenOnYes);
            }, this));
        }));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_delete.png"), bookLeft + 108, bookTop + 140, 0, 0, 9, 8, 9, 8);
        GuiUtils.drawOutlinedText(font, graphics, Component.translatable("blockprints.delete_confirm").getVisualOrderText(), bookLeft + 128, bookTop + 141);

    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/container_loading_status.png"), 0, 0, 0, 0, 305, 209, 305, 209);
        graphics.drawWordWrap(font, Component.literal("Delete Forever?"), 92, 77, 125, Constants.WHITE);
    }
}
