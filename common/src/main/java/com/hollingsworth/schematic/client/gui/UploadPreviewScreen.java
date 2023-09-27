package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.export.CameraSettings;
import com.hollingsworth.schematic.export.LytSize;
import com.hollingsworth.schematic.export.OffScreenRenderer;
import com.hollingsworth.schematic.export.Scene;
import com.hollingsworth.schematic.export.WrappedScene;
import com.hollingsworth.schematic.export.level.GuidebookLevel;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

import java.nio.file.Paths;

public class UploadPreviewScreen extends BaseSchematicScreen {

    public int yaw = 225;
    public int pitch = 30;
    public int roll;
    WrappedScene wrappedScene;
    Scene scene;
    OffScreenRenderer renderer;
    public UploadPreviewScreen() {
        super();
        wrappedScene = new WrappedScene();
        scene = new Scene(new GuidebookLevel(), new CameraSettings());
        scene.getCameraSettings().setIsometricYawPitchRoll(yaw, pitch, roll);
        wrappedScene.setScene(scene);
        wrappedScene.placeStructure(Paths.get("./schematics/test/test.nbt"));
        scene.getCameraSettings().setRotationCenter(scene.getWorldCenter());
        scene.centerScene();
    }

    public void updateSceneTexture() {
        scene.getCameraSettings().setIsometricYawPitchRoll(yaw, pitch, roll);
        scene.getCameraSettings().setRotationCenter(scene.getWorldCenter());

        scene.getCameraSettings().setZoom(1.0f);
        scene.centerScene();

        // Lazily create the renderer using the preferred size of the scene
        var prefSize = wrappedScene.getPreferredSize();
        final float scale = (float) Minecraft.getInstance().getWindow().getGuiScale();
        if (prefSize.width() > 0 && prefSize.height() > 0) {
            // We only scale the viewport, not scaling the view matrix means the scene will still fill it
            var renderWidth = (int) Math.max(1, prefSize.width() * scale);
            var renderHeight = (int) Math.max(1, prefSize.height() * scale);

            // Create/Re-Create Renderer if the desired render-size changed or if the renderer hasn't been created yet
            if (renderer == null || renderer.width != renderWidth || renderer.height != renderHeight) {
                if (renderer != null) {
                    renderer.close();
                }
                renderer = new OffScreenRenderer(renderWidth, renderHeight);
            }

            // Render the scene to the renderer's off-screen surface
            renderer.renderToTexture(() -> wrappedScene.renderToCurrentTarget(prefSize));
        }
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new ShortTextField(font, bookLeft + 185, bookTop + 41, Component.empty()));
        addRenderableWidget(new GuiImageButton(bookRight - 119, bookTop + 153, 95, 15, new ResourceLocation(Constants.MOD_ID, "textures/gui/button_small.png"), b ->{
        }));
        NoScrollMultiText description = new NoScrollMultiText(font, bookLeft + 185, bookTop + 73, 95, 78, Component.empty(), Component.empty());
        addRenderableWidget(description);
        addRenderableWidget(new DecoratedSlider(bookLeft + 41, bookTop + 152, 95, 15, Component.empty(), Component.empty(), 0, 360, 225, 5, 1, false, this::setYaw));
        addRenderableWidget(new DecoratedSlider(bookLeft + 41, bookTop + 168, 95, 15, Component.empty(), Component.empty(), 0, 90, 30, 5, 1, true, this::setPitch));
    }

    @Override
    public void removed() {
        super.removed();
        if (renderer != null) {
            renderer.close();
            renderer = null;
        }
    }

    public void setYaw(int yaw){
        this.yaw = yaw;
    }

    public void setPitch(int pitch){
        this.pitch = pitch;
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        updateSceneTexture();

        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        int previewX = bookLeft + 25;
        int previewY = bookTop + 41;
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/dialogue_model_preview.png"), previewX, previewY, 0, 0 , 143, 111, 143, 111);
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/icon_upload.png"), bookRight - 116, bookTop + 155, 0, 0, 9, 11, 9, 11);
        GuiUtils.drawCenteredOutlinedText(font, graphics, Component.translatable("blockprints.upload").getVisualOrderText(),  bookRight - 67, bookTop + 157);

        if (renderer != null) {
            int imageWidth = renderer.width;
            int imageHeight = renderer.height;

            // scale width and height to fit in the box of 100,100
            LytSize origDim = new LytSize(imageWidth, imageHeight);
            LytSize boundary = new LytSize(100, 100);
            LytSize newDim = getScaledDimension(origDim, boundary);
            // Offset x and Y so the image is centered
            // center x and y on point 50, 50
            int x = previewX + 143/2;
            int y = previewY + 111/2;
            x -= newDim.width() / 2;
            y -= newDim.height() / 2;

            innerBlit(graphics.pose(), renderer, x, x + newDim.width(), y, y + newDim.height(), 0);
        }
    }

    void innerBlit(PoseStack pose, OffScreenRenderer osr, int x1, int x2, int y1, int y2, int blitOffset) {
        RenderSystem.setShaderTexture(0, osr.fb.getColorTextureId());
        // Rest is same as GuiGraphics#
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        Matrix4f matrix4f = pose.last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        bufferBuilder.vertex(matrix4f, x1, y1, blitOffset).uv(0, 1).endVertex();
        bufferBuilder.vertex(matrix4f, x1, y2, blitOffset).uv(0, 0).endVertex();
        bufferBuilder.vertex(matrix4f, x2, y2, blitOffset).uv(1, 0).endVertex();
        bufferBuilder.vertex(matrix4f, x2, y1, blitOffset).uv(1, 1).endVertex();
        BufferUploader.drawWithShader(bufferBuilder.end());
    }

    public static LytSize getScaledDimension(LytSize imgSize, LytSize boundary) {

        int original_width = imgSize.width();
        int original_height = imgSize.height();
        int bound_width = boundary.width();
        int bound_height = boundary.height();
        int new_width = original_width;
        int new_height = original_height;

        // first check if we need to scale width
        if (original_width > bound_width) {
            //scale width to fit
            new_width = bound_width;
            //scale height to maintain aspect ratio
            new_height = (new_width * original_height) / original_width;
        }

        // then check if we need to scale even with the new height
        if (new_height > bound_height) {
            //scale height to fit instead
            new_height = bound_height;
            //scale width to maintain aspect ratio
            new_width = (new_height * original_width) / original_height;
        }

        return new LytSize(new_width, new_height);
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
