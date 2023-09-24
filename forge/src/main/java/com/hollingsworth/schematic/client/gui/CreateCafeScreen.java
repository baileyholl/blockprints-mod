package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.common.network.DownloadSchematic;
import com.hollingsworth.schematic.common.network.Networking;
import com.hollingsworth.schematic.export.CameraSettings;
import com.hollingsworth.schematic.export.Scene;
import com.hollingsworth.schematic.export.WrappedScene;
import com.hollingsworth.schematic.export.level.GuidebookLevel;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Paths;

public class CreateCafeScreen extends ModScreen {

    public static ResourceLocation background = new ResourceLocation(Constants.MOD_ID, "textures/gui/spell_book_template.png");

    public EditBox cafeName;
    public Button confirm;
    DynamicTexture dynamicTexture;
    public int yaw = 225;
    public int pitch = 30;
    public int roll;

    public CreateCafeScreen() {
        super(290, 194);
        buildTexture();
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
        Minecraft.getInstance().textureManager.register(new ResourceLocation(Constants.MOD_ID, "test_text"), dynamicTexture);
        System.out.println(dynamicTexture.getPixels().getHeight());
        System.out.println(dynamicTexture.getPixels().getWidth());
    }

    @Override
    public void init() {
        super.init();
        cafeName = new EditBox(font, bookLeft + 50, bookTop + 36, 80, 14, Component.empty());
        confirm = new ANButton(bookLeft + 40, bookTop + 150, 80, 20, Component.translatable("cafetier.create_schematic"), this::onCreate);
        cafeName.setMaxLength(64);
//        addRenderableWidget(new ANButton(bookLeft + 40, bookTop + 60, 80, 20, Component.translatable("cafetier.isometric_north_east"), button -> buildTexture(PerspectivePreset.ISOMETRIC_NORTH_EAST)));
//        addRenderableWidget(new ANButton(bookLeft + 40, bookTop + 80, 80, 20, Component.translatable("cafetier.isometric_north_west"), button -> buildTexture(PerspectivePreset.ISOMETRIC_NORTH_WEST)));
//        addRenderableWidget(new ANButton(bookLeft + 40, bookTop + 100, 80, 20, Component.translatable("cafetier.up"), button -> buildTexture(PerspectivePreset.UP)));

        addRenderableWidget(new BaseSlider(bookLeft + 200, bookTop + 30, 80, 20, Component.translatable("cafetier.yaw"), Component.empty(), 1, 360, 225, 5, 1, true) {
            @Override
            protected void applyValue() {
                super.applyValue();
                yaw = this.getValueInt();
                buildTexture();
            }
        });
        addRenderableWidget(new BaseSlider(bookLeft + 200, bookTop + 65, 80, 20, Component.translatable("cafetier.pitch"), Component.empty(), 1, 90, 30, 5, 1, true) {
            @Override
            protected void applyValue() {
                super.applyValue();
                pitch = this.getValueInt();
                buildTexture();
            }
        });

//        addRenderableWidget(cafeName);
        addRenderableWidget(confirm);
    }


    public void onCreate(Button button){
        Networking.sendToServer(new DownloadSchematic(cafeName.getValue()));
        minecraft.setScreen(null);
    }

    @Override
    public void render(GuiGraphics pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        int scaledHeight, scaledWidth;
        int maxWidth = 100;
        int maxHeight = 100;
        double aspectRatio = (double)dynamicTexture.getPixels().getWidth() / (double)dynamicTexture.getPixels().getHeight();
        if(maxWidth / maxHeight > aspectRatio){
            scaledWidth = (int)(maxHeight * aspectRatio);
            scaledHeight = maxHeight;
        }else{
            scaledWidth = maxWidth;
            scaledHeight = (int) (maxWidth / aspectRatio);
        }
        int x;
        int y;
        // center x and y on point 50, 50
        x = bookLeft + 100 - scaledWidth / 2;
        y = bookTop + 100 - scaledHeight / 2;

        PoseStack poseStack = pPoseStack.pose();
        poseStack.pushPose();
        // scale so the image fits in the box
        poseStack.scale((float)scaledWidth / (float)dynamicTexture.getPixels().getWidth(), (float)scaledHeight / (float)dynamicTexture.getPixels().getHeight(), 1);
        pPoseStack.blit(new ResourceLocation(Constants.MOD_ID, "test_text"), x,y, 0, 0, dynamicTexture.getPixels().getWidth(),  dynamicTexture.getPixels().getHeight(), dynamicTexture.getPixels().getWidth(),  dynamicTexture.getPixels().getHeight());
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getBgTexture() {
        return background;
    }

}
