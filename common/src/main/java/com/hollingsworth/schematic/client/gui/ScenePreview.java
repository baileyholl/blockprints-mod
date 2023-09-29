package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.export.LytSize;
import com.hollingsworth.schematic.export.OffScreenRenderer;
import com.hollingsworth.schematic.export.Scene;
import com.hollingsworth.schematic.export.WrappedScene;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class ScenePreview extends AbstractWidget {
    public int yaw = 225;
    public int pitch = 30;
    public int roll;
    WrappedScene wrappedScene;
    Scene scene;
    OffScreenRenderer renderer;
    public ScenePreview(int x, int y, int width, int height, Scene scene, WrappedScene wrappedScene) {
        super(x, y, width, height, Component.empty());
        this.wrappedScene = wrappedScene;
        this.scene = scene;
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
            renderer.renderToTexture(this::renderTarget);
        }
    }

    public void renderTarget(){
        wrappedScene.renderToCurrentTarget(wrappedScene.getPreferredSize());
    }

    public void removed(){
        if (renderer != null) {
            renderer.close();
            renderer = null;
        }
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        updateSceneTexture();
        int previewX = x;
        int previewY = y;

        if (renderer != null) {
            int imageWidth = renderer.width;
            int imageHeight = renderer.height;

            // scale width and height to fit in the box of 100,100
            LytSize origDim = new LytSize(imageWidth, imageHeight);
            LytSize boundary = new LytSize(width, height);
            LytSize newDim = getScaledDimension(origDim, boundary);
            // Offset x and Y so the image is centered
            // center x and y on point 50, 50
            int x = previewX + 143/2;
            int y = previewY + 111/2;
            x -= newDim.width() / 2;
            y -= newDim.height() / 2;

            innerBlit(graphics.pose(), renderer, x, x + newDim.width(), y, y + newDim.height(), 0);
        }
        graphics.blit(new ResourceLocation(Constants.MOD_ID, "textures/gui/gimbal/gimbal_cardinal.png"), x + 121, y + 5, 0, 0, 17, 17, 17, 17);
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
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
