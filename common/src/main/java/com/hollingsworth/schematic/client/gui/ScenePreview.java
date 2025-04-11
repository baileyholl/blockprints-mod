package com.hollingsworth.schematic.client.gui;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.renderer.*;
import com.hollingsworth.schematic.export.*;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ScenePreview extends NestedWidget {
    private int yaw = 225;
    private int pitch = 30;
    public WrappedScene wrappedScene;
    public Scene scene;
    public OffScreenRenderer renderer;
    FakeRenderingWorld fakeRenderingWorld;
    StructureRenderData cachedRender;
    HorizontalSlider yawSlider;
    HorizontalSlider pitchSlider;
    boolean doSort;
    int sortDebounce;

    public ScenePreview(int x, int y, int width, int height, WrappedScene wrappedScene, StructureTemplate template) {
        super(x, y, width, height, Component.empty());
        this.wrappedScene = wrappedScene;
        this.scene = wrappedScene.scene;
        cachedRender = new StructureRenderData(template, null, null);
        StructureRenderer.generateRender(cachedRender, Minecraft.getInstance().level, BlockPos.ZERO, 1f, new Vec3(0,0,0));
        scheduleSort();

        this.yawSlider = new HorizontalSlider(x + 16, y + height + 12, Component.empty(), Component.empty(), 0, 360, 225, 5, 1, false, this::setYaw);
        this.pitchSlider = new HorizontalSlider(x + 16, y + height + 28, Component.empty(), Component.empty(), 0, 90, 30, 5, 1, true, this::setPitch);
    }


    public void removed() {
        if (renderer != null) {
            renderer.close();
            renderer = null;
        }
    }

    public void tick(){
        if(doSort){
            if(sortDebounce > 0){
                sortDebounce--;
            }else{
                doSort = false;
                sortAll(cachedRender, new Vec3(0, 0, 0));
            }
        }
    }


    @Override
    protected void renderWidget(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        int previewX = x;
        int previewY = y;
        final float scale = (float) Minecraft.getInstance().getWindow().getGuiScale();

        scene.getCameraSettings().setIsometricYawPitchRoll(yaw, pitch, 0);
        scene.getCameraSettings().setRotationCenter(scene.getWorldCenter());

        scene.getCameraSettings().setZoom(1.0f);
        scene.centerScene();
        LytSize size = wrappedScene.getPreferredSize();
        scene.getCameraSettings().setViewportSize(size);


        CameraSettings cameraSettings = scene.getCameraSettings();
        Matrix4f projectionMatrix = cameraSettings.getProjectionMatrix();
        Matrix4f viewMatrix = cameraSettings.getViewMatrix();

        var prefSize = wrappedScene.getPreferredSize();
        // We only scale the viewport, not scaling the view matrix means the scene will still fill it
        var renderWidth = (int) Math.max(1, prefSize.width() * scale);
        var renderHeight = (int) Math.max(1, prefSize.height() * scale);

        LytSize origDim = new LytSize(renderWidth, renderHeight);
        LytSize boundary = new LytSize(width, height);
        LytSize newDim = getScaledDimension(origDim, boundary);
        int x = previewX + 143 / 2;
        int y = previewY + 143 / 2;
        x -= newDim.width() / 2;
        y -= newDim.height() / 2;


        RenderSystem.viewport((int) ((x) * scale), (int) (y * scale), (int) (newDim.width() * scale), (int) (newDim.height() * scale)); //The viewport is like a mini world where things get drawn
        RenderSystem.backupProjectionMatrix();

        RenderSystem.setProjectionMatrix(projectionMatrix, VertexSorting.ORTHOGRAPHIC_Z); //This is needed to switch to 3d rendering instead of 2d for the screen

        PoseStack poseStack = graphics.pose();
        poseStack.pushPose();
        poseStack.setIdentity();
        poseStack.mulPose(viewMatrix);
        RenderSystem.applyModelViewMatrix();
        RenderSystem.clear(GL11.GL_DEPTH_BUFFER_BIT, false); //Clear the depth buffer so it can draw where it is

        RenderSystem.runAsFancy(() -> {
            drawRenderScreen(poseStack, Minecraft.getInstance().player, cachedRender.statePosCache); //Draw VBO
        });

        poseStack.popPose(); //This should reset the view properly, but doesn't, hence the guiGraphics.flush() call before this method

        RenderSystem.applyModelViewMatrix();
        RenderSystem.viewport(0, 0, Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        RenderSystem.restoreProjectionMatrix();

        ModScreen.blitRect(graphics.pose(), x + 121, y + 3, 0, 0, 17, 17, 17, 17, ResourceLocation.fromNamespaceAndPath(Constants.MOD_ID, "textures/gui/gimbal/gimbal_cardinal.png"), 150);
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

    @Override
    public void playDownSound(SoundManager $$0) {

    }

    @Override
    public void addAfterParent(List<AbstractWidget> widgets) {
        widgets.add(new GimbalButton(x + 130, y + 5, "northeast", b -> {
            setYaw(225);
            setPitch(30);
        }));
        widgets.add(new GimbalButton(x + 124, y + 5, "northwest", b -> {
            setYaw(135);
            setPitch(30);
        }));
        widgets.add(new GimbalButton(x + 124, y + 11, "southwest", b -> {
            setYaw(45);
            setPitch(30);
        }));
        widgets.add(new GimbalButton(x + 130, y + 11, "southeast", b -> {
            setYaw(315);
            setPitch(30);
        }));
        widgets.add(yawSlider);
        widgets.add(pitchSlider);
    }

    public void drawRenderScreen(PoseStack matrix, Player player, ArrayList<StatePos> statePosCache){
        MultiBufferSource.BufferSource buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        //Draw the renders in the specified order
        ArrayList<RenderType> drawSet = new ArrayList<>();
        drawSet.add(RenderType.solid());
        drawSet.add(RenderType.cutout());
        drawSet.add(RenderType.cutoutMipped());
        drawSet.add(RenderType.translucent());
        drawSet.add(RenderType.tripwire());
        try {
            for (RenderType renderType : drawSet) {
                RenderType drawRenderType;
//                if (renderType.equals(RenderType.cutout()))
//                    drawRenderType = DireRenderTypes.RenderBlock;
//                else
                    drawRenderType = renderType;

                VertexBuffer vertexBuffer = cachedRender.vertexBuffers.get(renderType);
                if (vertexBuffer.getFormat() == null)
                    continue; //IDE says this is never null, but if we remove this check we crash because its null so....
                drawRenderType.setupRenderState();
                vertexBuffer.bind();
                vertexBuffer.drawWithShader(matrix.last().pose(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
                VertexBuffer.unbind();
                drawRenderType.clearRenderState();
            }
        } catch (Exception e) {
            System.out.println(e);
        }


        //if (true) return; //Remove this will render Tiles (Like chests) but remove tooltips - can't figure out how to fix tooltips!

        matrix.pushPose();
        matrix.setIdentity();
        DireRenderMethods.MultiplyAlphaRenderTypeBuffer multiplyAlphaRenderTypeBuffer = new DireRenderMethods.MultiplyAlphaRenderTypeBuffer(buffersource, 1f);
        //If any of the blocks in the render didn't have a model (like chests) we draw them here. This renders AND draws them, so more expensive than caching, but I don't think we have a choice

        if(fakeRenderingWorld == null){
            fakeRenderingWorld = new FakeRenderingWorld(Minecraft.getInstance().level, statePosCache, BlockPos.ZERO);
        }
        for (StatePos pos : statePosCache) {
            if (pos.state.isAir() || StructureRenderer.isModelRender(pos.state)) continue;
            matrix.pushPose();
            matrix.translate(pos.pos.getX(), pos.pos.getY(), pos.pos.getZ());
            BlockEntityRenderDispatcher blockEntityRenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            BlockEntity blockEntity = fakeRenderingWorld.getBlockEntity(pos.pos);
            if (blockEntity != null) {
                var renderer = blockEntityRenderer.getRenderer(blockEntity);
                try {
                    renderer.render(blockEntity, 0, matrix, multiplyAlphaRenderTypeBuffer, 15728640, OverlayTexture.NO_OVERLAY);
                } catch (Exception e) {
                    //No Op
                }
            } else {
                try {
                    DireRenderMethods.renderBETransparent(fakeRenderingWorld.getBlockState(pos.pos), matrix, buffersource, 15728640, 655360, 1.0f);
                } catch (Exception e) {
                    //No Op
                }
            }
            matrix.popPose();
        }
        matrix.popPose();

        buffersource.endLastBatch(); //Needed to draw the tiles at this point in the render pipeline or whatever - only for screens
    }

    //Sort all the RenderTypes
    public static void sortAll(StructureRenderData data, Vec3 projectedView) {
        for (Map.Entry<RenderType, MeshData.SortState> entry : data.sortStates.entrySet()) {
            RenderType renderType = entry.getKey();
            var renderedBuffer = sort(data, projectedView, renderType);
            VertexBuffer vertexBuffer = data.vertexBuffers.get(renderType);
            vertexBuffer.bind();
            vertexBuffer.uploadIndexBuffer(renderedBuffer);
            VertexBuffer.unbind();
        }
    }

    //Sort the render type we pass in - using DireBufferBuilder because we want to sort in the opposite direction from normal
    public static ByteBufferBuilder.Result sort(StructureRenderData data, Vec3 projectedView, RenderType renderType) {
        // Move our projected view in the direction of 0,0,0 by a tiny amount, accounting for negative values
        Vec3 inverted = projectedView.scale(-1);
        Vec3 subtracted = projectedView.add(inverted.normalize().scale(0.1));

        Vector3f sortPos = new Vector3f((float) -subtracted.x, (float)- subtracted.y, (float) -subtracted.z);
        return data.sortStates.get(renderType).buildSortedIndexBuffer(data.getByteBuffer(renderType), VertexSorting.byDistance(v -> -sortPos.distanceSquared(v)));
    }

    public void setYaw(int yaw) {
        this.yaw = yaw;
        yawSlider.setValue(yaw);
        scheduleSort();
    }


    public void setPitch(int pitch) {
        this.pitch = pitch;
        pitchSlider.setValue(pitch);
        scheduleSort();
    }

    private void scheduleSort(){
        doSort = true;
        sortDebounce = 20;
    }
}
