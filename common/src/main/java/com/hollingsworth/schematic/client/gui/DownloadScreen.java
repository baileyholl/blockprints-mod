package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.SchematicImporter;
import com.hollingsworth.schematic.client.ClientData;
import com.hollingsworth.schematic.common.util.ClientUtil;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DownloadScreen extends BaseSchematicScreen {
    public static final ResourceLocation PREVIEW_TEXTURE = new ResourceLocation(Constants.MOD_ID, "download_preview");
    DynamicTexture dynamicTexture;
    SchematicImporter.PreviewDownloadResult preview;
    boolean isDownloading;

    public DownloadScreen(SchematicImporter.PreviewDownloadResult preview) {
        super();
        this.preview = preview;
    }

    @Override
    public void init() {
        super.init();
        Path previewPath = preview.imagePath;
        try {
            NativeImage nativeImage = NativeImage.read(Files.newInputStream(previewPath, StandardOpenOption.READ));
            dynamicTexture = new DynamicTexture(nativeImage);
            Minecraft.getInstance().getTextureManager().register(PREVIEW_TEXTURE, dynamicTexture);
            addRenderableWidget(new PreviewImage(bookLeft + 25, bookTop + 41, 100, 100, dynamicTexture, PREVIEW_TEXTURE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        NoScrollMultiText descriptionField = new NoScrollMultiText(font, bookLeft + 185, bookTop + 73, 95, 78, Component.empty(), Component.empty());
        descriptionField.editable = false;
        descriptionField.setValue(preview.downloadResponse.description);
        addRenderableWidget(descriptionField);
        GuiImageButton uploadButton = new GuiImageButton(bookRight - 119, bookTop + 153, 95, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_small.png"), b -> {
            isDownloading = true;
            SchematicImporter.downloadSchematic(preview.downloadResponse.schematicLink, preview.downloadResponse.structureName).whenCompleteAsync((result, error) -> {
                isDownloading = false;
                Minecraft.getInstance().setScreen(null);
                if (result) {
                    ClientUtil.sendMessage("blockprints.download_success");
                } else {
                    ClientUtil.sendMessage("blockprints.download_failed");
                }
                ClientData.setStatus(Component.empty());
            }, Minecraft.getInstance());
        });
        addRenderableWidget(uploadButton);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_download.png"), bookRight - 116, bookTop + 155, 0, 0, 9, 11, 9, 11);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.download").getVisualOrderText(), bookRight - 67, bookTop + 157);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_model_preview.png"), 25, 41, 0, 0, 143, 111, 143, 111);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_long.png"), 25, 25, 0, 0, 143, 15, 143, 15);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_short.png"), 185, 25, 0, 0, 95, 15, 95, 15);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.literal(preview.downloadResponse.structureName).getVisualOrderText(), 25 + 143 / 2, 29);

    }

    @Override
    public void onClose() {
        if (isDownloading) {
            return;
        }
        super.onClose();
        Minecraft.getInstance().getTextureManager().release(PREVIEW_TEXTURE);
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !isDownloading && super.shouldCloseOnEsc();
    }
}
