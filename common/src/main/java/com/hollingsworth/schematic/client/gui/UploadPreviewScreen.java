package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.export.CameraSettings;
import com.hollingsworth.schematic.export.Scene;
import com.hollingsworth.schematic.export.WrappedScene;
import com.hollingsworth.schematic.export.level.GuidebookLevel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class UploadPreviewScreen extends BaseSchematicScreen {

    ScenePreview scenePreview;
    DecoratedSlider yawSlider;
    DecoratedSlider pitchSlider;
    public StructureTemplate structureTemplate;

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

        addRenderableWidget(new ShortTextField(font, bookLeft + 185, bookTop + 41, Component.empty()));
        addRenderableWidget(new GuiImageButton(bookRight - 119, bookTop + 153, 95, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_small.png"), b ->{
        }));
        NoScrollMultiText description = new NoScrollMultiText(font, bookLeft + 185, bookTop + 73, 95, 78, Component.empty(), Component.empty());
        addRenderableWidget(description);
        this.yawSlider = new DecoratedSlider(bookLeft + 41, bookTop + 152, 95, 15, Component.empty(), Component.empty(), 0, 360, 225, 5, 1, false, this::setYaw);
        this.pitchSlider = new DecoratedSlider(bookLeft + 41, bookTop + 168, 95, 15, Component.empty(), Component.empty(), 0, 90, 30, 5, 1, true, this::setPitch);
        addRenderableWidget(yawSlider);
        addRenderableWidget(pitchSlider);
        this.scenePreview = new ScenePreview(bookLeft + 25, bookTop + 41, 100, 100, scene, wrappedScene);
        scenePreview.yaw = 225;
        scenePreview.pitch = 30;
        addRenderableWidget(scenePreview);
        addRenderableWidget(new GimbalButton(bookLeft + 155, bookTop + 49, "northeast", b ->{
            setYaw(225);
            setPitch(30);
        }));
        addRenderableWidget(new GimbalButton(bookLeft + 149, bookTop + 49, "northwest", b ->{
            setYaw(135);
            setPitch(30);
        }));
        addRenderableWidget(new GimbalButton(bookLeft + 149, bookTop + 55, "southwest", b ->{
            setYaw(45);
            setPitch(30);
        }));
        addRenderableWidget(new GimbalButton(bookLeft + 155, bookTop + 55, "southeast", b ->{
            setYaw(315);
            setPitch(30);
        }));
    }

    @Override
    public void removed() {
        super.removed();
        scenePreview.removed();
    }

    public void setYaw(int yaw){
        scenePreview.yaw = yaw;
        yawSlider.setValue(yaw);
    }

    public void setPitch(int pitch){
        scenePreview.pitch = pitch;
        pitchSlider.setValue(pitch);
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_upload.png"), bookRight - 116, bookTop + 155, 0, 0, 9, 11, 9, 11);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.upload").getVisualOrderText(),  bookRight - 67, bookTop + 157);
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_model_preview.png"), 25, 41, 0, 0 , 143, 111, 143, 111);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_long.png"), 25, 25, 0, 0 , 143, 15, 143, 15);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_short.png"), 185, 25, 0, 0 , 95, 15, 95, 15);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_short.png"), 185, 57, 0, 0 , 95, 15, 95, 15);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.preview").getVisualOrderText(), 25 + 143/2, 29);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.name").getVisualOrderText(), 185 + 48, 29);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.description").getVisualOrderText(), 185 + 48, 61);
    }
}
