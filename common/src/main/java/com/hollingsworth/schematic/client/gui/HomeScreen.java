package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.GoogleCloudStorage;
import com.hollingsworth.schematic.api.blockprints.Upload;
import com.hollingsworth.schematic.client.ClientData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;

public class HomeScreen extends BaseSchematicScreen{
    public HomeScreen() {
        super();
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new GuiImageButton(bookLeft + 73, bookTop + 25, 159, 63, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_large_blue.png"), b ->{
            ClientData.showBoundary = true;
            ClientData.firstTarget = null;
            ClientData.secondTarget = null;
            Minecraft.getInstance().setScreen(null);
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable(Constants.MOD_ID + ".start_selecting"));
        }));
        addRenderableWidget(new GuiImageButton(bookLeft + 73, bookTop + 105, 159, 63, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_large_orange.png"), b ->{
            var response = Upload.postUpload("test", "test");
            var preview = response.signedImages[0];
            var schematic = response.signedSchematic;
            try {
                GoogleCloudStorage.uploadFileToGCS(URI.create(preview).toURL(), Paths.get("schematics/test0.png"), "image/png");
                GoogleCloudStorage.uploadFileToGCS(URI.create(schematic).toURL(), Paths.get("schematics/test/test.nbt"), "application/octet-stream");

            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_upload.png"), bookLeft + 76, bookTop + 27, 0, 0, 9, 11, 9, 11);

        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_download.png"), bookLeft + 76,bookTop + 107, 0, 0, 9, 11, 9, 11);
        GuiUtils.drawOutlinedText(font, graphics, Component.translatable("blockprints.upload").getVisualOrderText(), bookLeft + 93, bookTop + 29);
        GuiUtils.drawOutlinedText(font, graphics, Component.translatable("blockprints.download").getVisualOrderText(), bookLeft + 93, bookTop + 109);
        GuiUtils.drawOutlinedWordWrap(graphics, font, Component.translatable("blockprints.upload_desc"), bookLeft + 78, bookTop + 46, 150);
        GuiUtils.drawOutlinedWordWrap(graphics, font, Component.translatable("blockprints.download_desc"), bookLeft + 76, bookTop + 124, 150);
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
