package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.common.network.DownloadSchematic;
import com.hollingsworth.schematic.common.network.Networking;
import com.hollingsworth.schematic.export.WrappedScene;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class CreateCafeScreen extends ModScreen {

    public static ResourceLocation background = new ResourceLocation(Constants.MOD_ID, "textures/gui/spell_book_template.png");

    public EditBox cafeName;
    public Button confirm;
    DynamicTexture dynamicTexture;
    public CreateCafeScreen(WrappedScene wrappedScene) {
        super(290, 194);
        NativeImage nativeImage = wrappedScene.asNativeImage(1.0f);
        dynamicTexture = new DynamicTexture(nativeImage);
        Minecraft.getInstance().textureManager.register(new ResourceLocation(Constants.MOD_ID, "test_text"), dynamicTexture);

    }

    @Override
    public void init() {
        super.init();
        cafeName = new EditBox(font, bookLeft + 50, bookTop + 36, 80, 14, Component.empty());
        confirm = new ANButton(bookLeft + 40, bookTop + 150, 80, 20, Component.translatable("cafetier.create_schematic"), this::onCreate);
        cafeName.setMaxLength(64);
        addRenderableWidget(cafeName);
        addRenderableWidget(confirm);
    }


    public void onCreate(Button button){
        Networking.sendToServer(new DownloadSchematic(cafeName.getValue()));
        minecraft.setScreen(null);
    }

    @Override
    public void render(GuiGraphics pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        PoseStack poseStack = pPoseStack.pose();
        poseStack.pushPose();
//        poseStack.scale(0.25f, 0.25f, 0.25f);

        pPoseStack.blit(new ResourceLocation(Constants.MOD_ID, "test_text"), bookLeft + 40, bookTop + 60, 0, 0, dynamicTexture.getPixels().getWidth(),  dynamicTexture.getPixels().getHeight(), dynamicTexture.getPixels().getWidth(),  dynamicTexture.getPixels().getHeight());
        poseStack.popPose();
    }

    @Override
    public ResourceLocation getBgTexture() {
        return background;
    }

}
