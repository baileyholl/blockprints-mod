package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.blockprints.download.Download;
import com.hollingsworth.schematic.api.blockprints.download.PreviewDownloadResult;
import com.hollingsworth.schematic.api.blockprints.upload.Upload;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

import static com.hollingsworth.schematic.client.gui.DownloadScreen.PREVIEW_TEXTURE;

public class EditBuildScreen extends BaseSchematicScreen {
    DynamicTexture dynamicTexture;
    PreviewDownloadResult preview;
    Screen previousScreen;
    ShortTextField nameField;
    NoScrollMultiText descriptionField;
    GuiImageButton uploadButton;
    public boolean makePublic = false;

    public EditBuildScreen(Screen previousScreen, PreviewDownloadResult preview) {
        super();
        this.previousScreen = previousScreen;
        this.preview = preview;
        this.makePublic = preview.downloadResponse.isPublic;
    }

    public static LoadingScreen<PreviewDownloadResult> getTransition(String code, Screen previousScreen) {
        return new LoadingScreen<>(() -> Download.downloadPreview(code), (build) -> {
            Minecraft.getInstance().setScreen(new EditBuildScreen(previousScreen, build));
        }, previousScreen);
    }

    @Override
    public void init() {
        super.init();

        try {
            dynamicTexture = DownloadScreen.getTexture(preview);
            Minecraft.getInstance().getTextureManager().register(PREVIEW_TEXTURE, dynamicTexture);
            addRenderableWidget(new PreviewImage(bookLeft + 25, bookTop + 41, 100, 100, dynamicTexture, PREVIEW_TEXTURE));
        } catch (Exception e) {
            e.printStackTrace();
        }

        nameField = new ShortTextField(font, bookLeft + 185, bookTop + 39, Component.empty());
        descriptionField = new NoScrollMultiText(font, bookLeft + 185, bookTop + 71, 95, 81, Component.empty(), Component.empty());
        nameField.setValue(preview.downloadResponse.structureName);
        descriptionField.setValue(preview.downloadResponse.description);
        uploadButton = new GuiImageButton(bookRight - 119, bookTop + 169, 95, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_6.png"), b -> {
            var name = nameField.getValue().trim();
            var desc = descriptionField.getValue().trim();
            // return if the name or description is too long or too short
            if (name.length() > UploadPreviewScreen.MAX_NAME_LENGTH || name.length() < UploadPreviewScreen.MIN_NAME_LENGTH || desc.length() > UploadPreviewScreen.MAX_DESC_LENGTH || desc.length() < UploadPreviewScreen.MIN_DESC_LENGTH) {
                return;
            }
            Minecraft.getInstance().setScreen(new LoadingScreen<>(() -> Upload.postEdit(preview.downloadResponse.id, name, desc, this.makePublic), (build) -> {
                if (previousScreen instanceof ViewFavoritesScreen buildsScreen) {
                    Minecraft.getInstance().setScreen(ViewFavoritesScreen.getTransition(buildsScreen.showFavorites, buildsScreen.showBuilds, buildsScreen.showRecent));
                } else {
                    Minecraft.getInstance().setScreen(ViewFavoritesScreen.getTransition());
                }
            }, this));
        });
        addRenderableWidget(uploadButton);

        addRenderableWidget(new GuiImageButton(bookLeft + 9, bookTop + 9, 15, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_back.png"), b -> {
            Minecraft.getInstance().setScreen(previousScreen);
        }));

        addRenderableWidget(nameField);
        addRenderableWidget(descriptionField);
        addRenderableWidget(new CheckBoxButton(bookRight - 119, bookTop + 153, b -> {
            this.makePublic = !this.makePublic;
        }, () -> this.makePublic).withTooltip(Component.translatable("blockprints.make_public_tooltip")));

    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_upload.png"), bookRight - 116, bookTop + 171, 0, 0, 9, 11, 9, 11);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.make_public").getVisualOrderText(), bookRight - 67, bookTop + 157);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.save").getVisualOrderText(), bookRight - 67, bookTop + 173);
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
    public void removed() {
        super.removed();
        Minecraft.getInstance().getTextureManager().release(PREVIEW_TEXTURE);
    }

    @Override
    public void collectTooltips(GuiGraphics stack, int mouseX, int mouseY, List<Component> tooltip) {
        super.collectTooltips(stack, mouseX, mouseY, tooltip);
        if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, uploadButton)) {
            var name = nameField.getValue().trim();
            var desc = descriptionField.getValue().trim();
            if (name.length() > UploadPreviewScreen.MAX_NAME_LENGTH) {
                tooltip.add(Component.translatable("blockprints.name_too_long"));
            }
            if (name.length() < UploadPreviewScreen.MIN_NAME_LENGTH) {
                tooltip.add(Component.translatable("blockprints.name_too_short"));
            }
            if (desc.length() > UploadPreviewScreen.MAX_DESC_LENGTH) {
                tooltip.add(Component.translatable("blockprints.description_too_long"));
            }
            if (desc.length() < UploadPreviewScreen.MIN_DESC_LENGTH) {
                tooltip.add(Component.translatable("blockprints.description_too_short"));
            }
        }
    }
}
