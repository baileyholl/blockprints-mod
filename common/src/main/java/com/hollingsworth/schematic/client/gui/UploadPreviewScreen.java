package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.api.SceneExporter;
import com.hollingsworth.schematic.export.CameraSettings;
import com.hollingsworth.schematic.export.Scene;
import com.hollingsworth.schematic.export.WrappedScene;
import com.hollingsworth.schematic.export.level.GuidebookLevel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.io.IOException;
import java.util.List;

public class UploadPreviewScreen extends BaseSchematicScreen {

    ScenePreview scenePreview;
    HorizontalSlider yawSlider;
    HorizontalSlider pitchSlider;
    ShortTextField nameField;
    NoScrollMultiText descriptionField;
    GuiImageButton uploadButton;
    public StructureTemplate structureTemplate;
    public static final int MAX_NAME_LENGTH = 100;
    public static final int MIN_NAME_LENGTH = 10;
    public static final int MAX_DESC_LENGTH = 1000;
    public static final int MIN_DESC_LENGTH = 20;

    public UploadPreviewScreen(StructureTemplate structureTemplate) {
        super();
        this.structureTemplate = structureTemplate;
    }

    @Override
    public void init() {
        super.init();
        WrappedScene wrappedScene = new WrappedScene();
        Scene scene = new Scene(new GuidebookLevel(), new CameraSettings());
        wrappedScene.setScene(scene);
        wrappedScene.placeStructure(structureTemplate);
        nameField = new ShortTextField(font, bookLeft + 185, bookTop + 39, Component.empty());
        descriptionField = new NoScrollMultiText(font, bookLeft + 185, bookTop + 71, 95, 81, Component.empty(), Component.empty());
        uploadButton = new GuiImageButton(bookRight - 119, bookTop + 153, 95, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_6.png"), b -> {
            var name = nameField.getValue().trim();
            var desc = descriptionField.getValue().trim();
            // return if the name or description is too long or too short
            if (name.length() > MAX_NAME_LENGTH || name.length() < MIN_NAME_LENGTH || desc.length() > MAX_DESC_LENGTH || desc.length() < MIN_DESC_LENGTH) {
                return;
            }
            SceneExporter sceneExporter = new SceneExporter(wrappedScene, structureTemplate);
            try {
                sceneExporter.exportLocally(this.nameField.getValue(), this.descriptionField.getValue());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        addRenderableWidget(uploadButton);
        addRenderableWidget(nameField);
        addRenderableWidget(descriptionField);
        this.yawSlider = new HorizontalSlider(bookLeft + 41, bookTop + 153, Component.empty(), Component.empty(), 0, 360, 225, 5, 1, false, this::setYaw);
        this.pitchSlider = new HorizontalSlider(bookLeft + 41, bookTop + 169, Component.empty(), Component.empty(), 0, 90, 30, 5, 1, true, this::setPitch);
        addRenderableWidget(yawSlider);
        addRenderableWidget(pitchSlider);
        this.scenePreview = new ScenePreview(bookLeft + 25, bookTop + 41, 100, 100, scene, wrappedScene);
        scenePreview.yaw = 225;
        scenePreview.pitch = 30;
        addRenderableWidget(scenePreview);
        addRenderableWidget(new GimbalButton(bookLeft + 155, bookTop + 47, "northeast", b -> {
            setYaw(225);
            setPitch(30);
        }));
        addRenderableWidget(new GimbalButton(bookLeft + 149, bookTop + 47, "northwest", b -> {
            setYaw(135);
            setPitch(30);
        }));
        addRenderableWidget(new GimbalButton(bookLeft + 149, bookTop + 53, "southwest", b -> {
            setYaw(45);
            setPitch(30);
        }));
        addRenderableWidget(new GimbalButton(bookLeft + 155, bookTop + 53, "southeast", b -> {
            setYaw(315);
            setPitch(30);
        }));
        addRenderableWidget(new GuiImageButton(bookLeft + 9, bookTop + 9, 15, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_back.png"), b -> {
            Minecraft.getInstance().setScreen(new HomeScreen());
        }));
    }

    @Override
    public void removed() {
        super.removed();
        scenePreview.removed();
    }

    public void setYaw(int yaw) {
        scenePreview.yaw = yaw;
        yawSlider.setValue(yaw);
    }

    public void setPitch(int pitch) {
        scenePreview.pitch = pitch;
        pitchSlider.setValue(pitch);
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_upload.png"), bookRight - 116, bookTop + 155, 0, 0, 9, 11, 9, 11);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.upload").getVisualOrderText(), bookRight - 67, bookTop + 157);
    }

    @Override
    public void collectTooltips(GuiGraphics stack, int mouseX, int mouseY, List<Component> tooltip) {
        super.collectTooltips(stack, mouseX, mouseY, tooltip);
        if (GuiUtils.isMouseInRelativeRange(mouseX, mouseY, uploadButton)) {
            var name = nameField.getValue().trim();
            var desc = descriptionField.getValue().trim();
            if (name.length() > MAX_NAME_LENGTH) {
                tooltip.add(Component.translatable("blockprints.name_too_long"));
            }
            if (name.length() < MIN_NAME_LENGTH) {
                tooltip.add(Component.translatable("blockprints.name_too_short"));
            }
            if (desc.length() > MAX_DESC_LENGTH) {
                tooltip.add(Component.translatable("blockprints.description_too_long"));
            }
            if (desc.length() < MIN_DESC_LENGTH) {
                tooltip.add(Component.translatable("blockprints.description_too_short"));
            }
        }
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/diologue_preview.png"), 25, 25, 0, 0, 143, 127, 143, 127);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/diologue_title.png"), 185, 25, 0, 0, 95, 14, 95, 14);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/diologue_title.png"), 185, 57, 0, 0, 95, 14, 95, 14);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.set_preview").getVisualOrderText(), 25 + 143 / 2, 29);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.name").getVisualOrderText(), 185 + 48, 29);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.description").getVisualOrderText(), 185 + 48, 61);
    }
}
