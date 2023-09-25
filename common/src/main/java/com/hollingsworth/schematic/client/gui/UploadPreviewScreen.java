package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.export.CameraSettings;
import com.hollingsworth.schematic.export.Scene;
import com.hollingsworth.schematic.export.WrappedScene;
import com.hollingsworth.schematic.export.level.GuidebookLevel;
import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Paths;

public class UploadPreviewScreen extends BaseSchematicScreen {

    DynamicTexture dynamicTexture;
    public int yaw = 225;
    public int pitch = 30;
    public int roll;

    public UploadPreviewScreen() {
        super();
//        buildTexture();
    }

    public void buildTexture(){
        WrappedScene wrappedScene = new WrappedScene();
        Scene scene = new Scene(new GuidebookLevel(), new CameraSettings());
        scene.getCameraSettings().setIsometricYawPitchRoll(yaw, pitch, roll);
        wrappedScene.setScene(scene);
        wrappedScene.placeStructure(Paths.get("./schematics/test/test.nbt"));
        scene.getCameraSettings().setRotationCenter(scene.getWorldCenter());
        scene.centerScene();
        NativeImage nativeImage = wrappedScene.asNativeImage(1.0f);
        dynamicTexture = new DynamicTexture(nativeImage);
        Minecraft.getInstance().getTextureManager().register(new ResourceLocation(Constants.MOD_ID, "test_text"), dynamicTexture);
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new ShortTextField(font, bookLeft + 185, bookTop + 41, Component.empty()));
        addRenderableWidget(new GuiImageButton(bookRight - 119, bookTop + 153, 95, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_small.png"), b ->{
        }));
//        cafeName = new EditBox(font, bookLeft + 50, bookTop + 36, 80, 14, Component.empty());
//        new ANButton(bookLeft + 40, bookTop + 150, 80, 20, Component.translatable("cafetier.create_schematic"), this::onCreate);
//        cafeName.setMaxLength(64);

//        addRenderableWidget(new BaseSlider(bookLeft + 200, bookTop + 30, 80, 20, Component.translatable("cafetier.yaw"), Component.empty(), 1, 360, 225, 5, 1, true) {
//            @Override
//            protected void applyValue() {
//                super.applyValue();
//                yaw = this.getValueInt();
//                buildTexture();
//            }
//        });
//        addRenderableWidget(new BaseSlider(bookLeft + 200, bookTop + 65, 80, 20, Component.translatable("cafetier.pitch"), Component.empty(), 1, 90, 30, 5, 1, true) {
//            @Override
//            protected void applyValue() {
//                super.applyValue();
//                pitch = this.getValueInt();
//                buildTexture();
//            }
//        });
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_model_preview.png"),bookLeft + 25, bookTop + 41, 0, 0 , 143, 111, 143, 111);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_upload.png"), bookRight - 116, bookTop + 155, 0, 0, 9, 11, 9, 11);
        GuiUtils.drawOutlinedText(font, graphics, Component.translatable("blockprints.upload").getVisualOrderText(),  bookRight - 99, bookTop + 157);
//        int scaledHeight, scaledWidth;
//        int maxWidth = 100;
//        int maxHeight = 100;
//        double aspectRatio = (double)dynamicTexture.getPixels().getWidth() / (double)dynamicTexture.getPixels().getHeight();
//        if(maxWidth / maxHeight > aspectRatio){
//            scaledWidth = (int)(maxHeight * aspectRatio);
//            scaledHeight = maxHeight;
//        }else{
//            scaledWidth = maxWidth;
//            scaledHeight = (int) (maxWidth / aspectRatio);
//        }
//        int x;
//        int y;
//        // center x and y on point 50, 50
//        x = bookLeft + 100 - scaledWidth / 2;
//        y = bookTop + 100 - scaledHeight / 2;
//
//        PoseStack poseStack = graphics.pose();
//        poseStack.pushPose();
//        // scale so the image fits in the box
//        poseStack.scale((float)scaledWidth / (float)dynamicTexture.getPixels().getWidth(), (float)scaledHeight / (float)dynamicTexture.getPixels().getHeight(), 1);
//        graphics.blit(new ResourceLocation(Constants.MOD_ID, "test_text"), x,y, 0, 0, dynamicTexture.getPixels().getWidth(),  dynamicTexture.getPixels().getHeight(), dynamicTexture.getPixels().getWidth(),  dynamicTexture.getPixels().getHeight());
//        poseStack.popPose();
    }

    @Override
    public void drawBackgroundElements(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.drawBackgroundElements(graphics, mouseX, mouseY, partialTicks);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_long.png"), 25, 25, 0, 0 , 143, 15, 143, 15);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_short.png"), 185, 25, 0, 0 , 95, 15, 95, 15);

        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_short.png"), 185, 57, 0, 0 , 95, 15, 95, 15);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.preview").getVisualOrderText(), 25 + 143/2, 29);

        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.name").getVisualOrderText(), 185 + 48, 29);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.description").getVisualOrderText(), 185 + 48, 61);

    }
}