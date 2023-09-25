package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class HomeScreen extends BaseSchematicScreen{
    public HomeScreen() {
        super();
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new GuiImageButton(bookLeft + 73, bookTop + 25, 159, 63, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_large_blue.png"), b ->{
            Minecraft.getInstance().setScreen(new UploadPreviewScreen());
        }));
        addRenderableWidget(new GuiImageButton(bookLeft + 73, bookTop + 105, 159, 63, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_large_orange.png"), b ->{

        }));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_upload.png"), bookLeft + 76, bookTop + 27, 0, 0, 9, 11, 9, 11);

        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_download.png"), bookLeft + 76,bookTop + 107, 0, 0, 9, 11, 9, 11);
        graphics.drawString(font, Component.translatable("blockprints.upload"), bookLeft + 93, bookTop + 29, 16777215, false);
        graphics.drawString(font, Component.translatable("blockprints.download"), bookLeft + 93, bookTop + 109, 16777215, false);
        graphics.drawWordWrap(font, Component.translatable("blockprints.upload_desc"), bookLeft + 76, bookTop + 44, 150, 16777215);
        graphics.drawWordWrap(font, Component.translatable("blockprints.download_desc"), bookLeft + 76, bookTop + 124, 150, 16777215);
    }

    @Override
    public void drawForegroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawForegroundElements(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
    }
}
