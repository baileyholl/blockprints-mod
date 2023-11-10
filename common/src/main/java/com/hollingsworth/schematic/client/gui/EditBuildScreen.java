package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.download.Download;
import com.hollingsworth.schematic.api.blockprints.download.PreviewDownloadResult;
import com.hollingsworth.schematic.client.ClientData;
import com.hollingsworth.schematic.common.util.ClientUtil;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class EditBuildScreen extends BaseSchematicScreen {
    public static final ResourceLocation PREVIEW_TEXTURE = new ResourceLocation(Constants.MOD_ID, "download_preview");
    DynamicTexture dynamicTexture;
    PreviewDownloadResult preview;
    Screen previousScreen;
    ShortTextField nameField;
    NoScrollMultiText descriptionField;

    public EditBuildScreen(Screen previousScreen, PreviewDownloadResult preview) {
        super();
        this.previousScreen = previousScreen;
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

        nameField = new ShortTextField(font, bookLeft + 185, bookTop + 39, Component.empty());
        descriptionField = new NoScrollMultiText(font, bookLeft + 185, bookTop + 71, 95, 81, Component.empty(), Component.empty());
        nameField.setValue(preview.downloadResponse.structureName);
        descriptionField.setValue(preview.downloadResponse.description);
        addRenderableWidget(new GuiImageButton(bookRight - 119, bookTop + 153, 95, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_6.png"), b -> {
            Minecraft.getInstance().setScreen(new LoadingScreen<>(() -> Download.downloadSchematic(preview.downloadResponse.schematicLink, preview.downloadResponse.structureName), result -> {
                Minecraft.getInstance().setScreen(null);
                if (result) {
                    ClientUtil.sendMessage("blockprints.download_success");
                } else {
                    ClientUtil.sendMessage("blockprints.download_failed");
                }
                ClientData.setStatus(Component.empty());
            }));
        }));

        addRenderableWidget(new GuiImageButton(bookLeft + 9, bookTop + 9, 15, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_back.png"), b -> {
            Minecraft.getInstance().setScreen(previousScreen);
        }));

        addRenderableWidget(nameField);
        addRenderableWidget(descriptionField);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_upload.png"), bookRight - 116, bookTop + 155, 0, 0, 9, 11, 9, 11);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.save").getVisualOrderText(), bookRight - 67, bookTop + 157);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/diologue_preview.png"), 25, 25, 0, 0, 143, 127, 143, 127);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/diologue_title.png"), 185, 25, 0, 0, 95, 14, 95, 14);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/diologue_title.png"), 185, 57, 0, 0, 95, 14, 95, 14);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.preview").getVisualOrderText(), 25 + 143 / 2, 29);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.name").getVisualOrderText(), 185 + 48, 29);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.description").getVisualOrderText(), 185 + 48, 61);
    }

    @Override
    public void onClose() {
        super.onClose();
        Minecraft.getInstance().getTextureManager().release(PREVIEW_TEXTURE);
    }
}
