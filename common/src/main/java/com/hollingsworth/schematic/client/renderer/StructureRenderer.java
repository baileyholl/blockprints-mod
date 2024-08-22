package com.hollingsworth.schematic.client.renderer;

import com.hollingsworth.schematic.client.RaycastHelper;
import com.hollingsworth.schematic.common.util.Color;
import com.hollingsworth.schematic.common.util.DimPos;
import com.hollingsworth.schematic.platform.Services;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Map;

public class StructureRenderer {
    public static ArrayList<StructureRenderData> structures = new ArrayList<>();

    //Start rendering - this is the most expensive part, so we render it, then cache it, and draw it over and over (much cheaper)
    public static void buildRender(StructureRenderData data, PoseStack poseStack, Player player) {
        BlockHitResult lookingAt = RaycastHelper.getLookingAt(player, true);
        BlockPos renderPos = data.anchorPos == null ? lookingAt.getBlockPos() : data.anchorPos;
        if(renderPos == null){
            return;
        }
        renderPos = renderPos.above();
        DimPos boundTo = new DimPos(player.level.dimension(), renderPos);
        if (boundTo != null && boundTo.levelKey().equals(player.level().dimension())) {
            drawBoundBox(data, poseStack, boundTo.pos());
        }


        renderPos = renderPos.above();
        //Start drawing the Render and cache it, used for both Building and Copy/Paste
        if (shouldUpdateRender(data, renderPos)) {
            generateRender(data, player.level(), renderPos, 0.5f, data.statePosCache, data.vertexBuffers);
            data.lastRenderPos = renderPos;
        }
    }

    public static boolean shouldUpdateRender(StructureRenderData data, BlockPos renderPos) {
        if(data.lastRenderPos == null || !data.lastRenderPos.equals(renderPos)){
            return true;
        }
        return false;
    }
    public static void clearByteBuffers(StructureRenderData data) { //Prevents leaks - Unused?
        for (Map.Entry<RenderType, ByteBufferBuilder> entry : data.builders.entrySet()) {
            entry.getValue().clear();
        }
        data.bufferBuilders.clear();
        data.sortStates.clear();
        data.meshDatas.clear();
//        data.vertexBuffers = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((renderType) -> renderType, (type) -> new VertexBuffer(VertexBuffer.Usage.STATIC)));
    }
    /**
     * This method creates a Map<RenderType, VertexBuffer> when given an ArrayList<StatePos> statePosCache - its used both here to draw in-game AND in the TemplateManagerGUI.java class
     */
    public static void generateRender(StructureRenderData data, Level level, BlockPos renderPos, float transparency, ArrayList<StatePos> statePosCache, Map<RenderType, VertexBuffer> vertexBuffers) {
        if (statePosCache == null || statePosCache.isEmpty()) return;
        data.fakeRenderingWorld = new FakeRenderingWorld(level, statePosCache, renderPos);
        PoseStack matrix = new PoseStack(); //Create a new matrix stack for use in the buffer building process
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        ModelBlockRenderer modelBlockRenderer = dispatcher.getModelRenderer();
        final RandomSource random = RandomSource.create();
        clearByteBuffers(data);
        //Iterate through the state pos cache and start drawing to the VertexBuffers - skip modelRenders(like chests) - include fluids (even though they don't work yet)
        for (StatePos pos : statePosCache.stream().filter(pos -> isModelRender(pos.state) || !pos.state.getFluidState().isEmpty()).toList()) {
            BlockState renderState = data.fakeRenderingWorld.getBlockStateWithoutReal(pos.pos);
            if (renderState.isAir()) continue;

            BakedModel ibakedmodel = dispatcher.getBlockModel(renderState);
            matrix.pushPose();
            matrix.translate(pos.pos.getX(), pos.pos.getY(), pos.pos.getZ());

            for (RenderType renderType : Services.PLATFORM.getRenderTypes(ibakedmodel, renderState, random)) {
                //Flowers render weirdly so we use a custom renderer to make them look better. Glass and Flowers are both cutouts, so we only want this for non-cube blocks
                if (renderType.equals(RenderType.cutout()) && renderState.getShape(level, pos.pos.offset(renderPos)).equals(Shapes.block()))
                    renderType = RenderType.translucent();
                BufferBuilder builder = data.bufferBuilders.computeIfAbsent(renderType, rt -> new BufferBuilder(data.getByteBuffer(rt), rt.mode(), rt.format()));
                DireVertexConsumer direVertexConsumer = new DireVertexConsumer(builder, transparency);
                //Use tesselateBlock to skip the block.isModel check - this helps render Create blocks that are both models AND animated
                if (renderState.getFluidState().isEmpty()) {
                    try {
                        modelBlockRenderer.tesselateBlock(data.fakeRenderingWorld, ibakedmodel, renderState, pos.pos.offset(renderPos).above(255), matrix, direVertexConsumer, false, random, renderState.getSeed(pos.pos.offset(renderPos)), OverlayTexture.NO_OVERLAY);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //System.out.println(e);
                    }
                } else {
                    try {
                        RenderFluidBlock.renderFluidBlock(renderState, level, pos.pos.offset(renderPos).above(255), matrix, direVertexConsumer, true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
            matrix.popPose();
        }
        //Sort all the builder's vertices and then upload them to the vertex buffers
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 subtracted = projectedView.subtract(renderPos.getX(), renderPos.getY(), renderPos.getZ());
        Vector3f sortPos = new Vector3f((float) subtracted.x, (float) subtracted.y, (float) subtracted.z);
        for (Map.Entry<RenderType, BufferBuilder> entry : data.bufferBuilders.entrySet()) {
            RenderType renderType = entry.getKey();
            ByteBufferBuilder byteBufferBuilder = data.getByteBuffer(renderType);
            BufferBuilder builder = entry.getValue();
            var meshDatas = data.meshDatas;
            if (meshDatas.containsKey(renderType) && meshDatas.get(renderType) != null)
                meshDatas.get(renderType).close();
            meshDatas.put(renderType, builder.build());
            if (meshDatas.containsKey(renderType) && meshDatas.get(renderType) != null) {
                data.sortStates.put(renderType, meshDatas.get(renderType).sortQuads(byteBufferBuilder, VertexSorting.byDistance(v -> -sortPos.distanceSquared(v))));
                VertexBuffer vertexBuffer = vertexBuffers.get(entry.getKey());
                vertexBuffer.bind();
                vertexBuffer.upload(meshDatas.get(renderType));
                VertexBuffer.unbind();
            }
        }
    }

    public static void drawBoundBox(StructureRenderData data, PoseStack matrix, BlockPos blockPos) {
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        matrix.pushPose();
        matrix.translate(-projectedView.x(), -projectedView.y(), -projectedView.z());
        Color color = Color.BLUE;
        var boundingBox = data.boundingBox;
        if(boundingBox != null){
            BlockPos min = new BlockPos(boundingBox.minX(), boundingBox.minY(), boundingBox.minZ());
            BlockPos max = new BlockPos(boundingBox.maxX(), boundingBox.maxY(), boundingBox.maxZ());
            min = min.offset(blockPos);
            max = max.offset(blockPos);
            DireRenderMethods.renderCopy(matrix, min, max, color);
        }
        matrix.popPose();
    }

    public static boolean isModelRender(BlockState state) {
        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        BakedModel ibakedmodel = dispatcher.getBlockModel(state);
        for (Direction direction : Direction.values()) {
            if (!ibakedmodel.getQuads(state, direction, RandomSource.create()).isEmpty()) {
                return true;
            }
            if (!ibakedmodel.getQuads(state, null, RandomSource.create()).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    //Draw what we've cached
    public static void drawRender(StructureRenderData data, PoseStack poseStack, Matrix4f projectionMatrix, Matrix4f modelViewMatrix, Player player) {
        if (data.vertexBuffers == null) {
            return;
        }
        BlockPos anchorPos = data.anchorPos;
        MultiBufferSource.BufferSource buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        BlockHitResult lookingAt = RaycastHelper.getLookingAt(player, true);
        BlockPos renderPos = anchorPos == null ? lookingAt.getBlockPos() : anchorPos;
        BlockState lookingAtState = player.level().getBlockState(renderPos);

        if (lookingAtState.isAir() && anchorPos == null)
            return;
        renderPos = renderPos.above();
        //Sort every <X> Frames to prevent screendoor effect
        if (data.sortCounter > 20) {
            sortAll(data, renderPos);
            data.sortCounter = 0;
        } else {
            data.sortCounter++;
        }

        PoseStack matrix = poseStack;
        matrix.pushPose();
        matrix.mulPose(modelViewMatrix);
        matrix.translate(-projectedView.x(), -projectedView.y(), -projectedView.z());
        matrix.translate(renderPos.getX(), renderPos.getY(), renderPos.getZ());
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
                if (renderType.equals(RenderType.cutout()))
                    drawRenderType = DireRenderTypes.RenderBlock;
                else
                    drawRenderType = RenderType.translucent();
                VertexBuffer vertexBuffer = data.vertexBuffers.get(renderType);
                if (vertexBuffer.getFormat() == null)
                    continue; //IDE says this is never null, but if we remove this check we crash because its null so....
                drawRenderType.setupRenderState();
                vertexBuffer.bind();
                vertexBuffer.drawWithShader(matrix.last().pose(), new Matrix4f(projectionMatrix), RenderSystem.getShader());
                VertexBuffer.unbind();
                drawRenderType.clearRenderState();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        matrix.popPose();

        BlockRenderDispatcher dispatcher = Minecraft.getInstance().getBlockRenderer();
        DireRenderMethods.MultiplyAlphaRenderTypeBuffer multiplyAlphaRenderTypeBuffer = new DireRenderMethods.MultiplyAlphaRenderTypeBuffer(buffersource, 0.5f);
        //If any of the blocks in the render didn't have a model (like chests) we draw them here. This renders AND draws them, so more expensive than caching, but I don't think we have a choice
        data.fakeRenderingWorld = new FakeRenderingWorld(player.level(), data.statePosCache, renderPos);
        for (StatePos pos : data.statePosCache.stream().filter(pos -> !isModelRender(pos.state)).toList()) {
            if (pos.state.isAir()) continue;
            matrix.pushPose();
            matrix.translate(-projectedView.x(), -projectedView.y(), -projectedView.z());
            matrix.translate(renderPos.getX(), renderPos.getY(), renderPos.getZ());
            matrix.translate(pos.pos.getX(), pos.pos.getY(), pos.pos.getZ());
            //MyRenderMethods.renderBETransparent(mockBuilderWorld.getBlockState(pos.pos), matrix, buffersource, 15728640, 655360, 0.5f);
            BlockEntityRenderDispatcher blockEntityRenderer = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            BlockEntity blockEntity = data.fakeRenderingWorld.getBlockEntity(pos.pos);
            if (blockEntity != null)
                blockEntityRenderer.render(blockEntity, 0, matrix, multiplyAlphaRenderTypeBuffer);
            else
                DireRenderMethods.renderBETransparent(data.fakeRenderingWorld.getBlockState(pos.pos), matrix, buffersource, 15728640, 655360, 0.5f);
            matrix.popPose();
        }
    }

    //Sort all the RenderTypes
    public static void sortAll(StructureRenderData data, BlockPos lookingAt) {
        for (Map.Entry<RenderType, MeshData.SortState> entry : data.sortStates.entrySet()) {
            RenderType renderType = entry.getKey();
            var renderedBuffer = sort(data, lookingAt, renderType);
            VertexBuffer vertexBuffer = data.vertexBuffers.get(renderType);
            vertexBuffer.bind();
            vertexBuffer.uploadIndexBuffer(renderedBuffer);
            VertexBuffer.unbind();
        }
    }

    //Sort the render type we pass in - using DireBufferBuilder because we want to sort in the opposite direction from normal
    public static ByteBufferBuilder.Result sort(StructureRenderData data, BlockPos lookingAt, RenderType renderType) {
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 subtracted = projectedView.subtract(lookingAt.getX(), lookingAt.getY(), lookingAt.getZ());
        Vector3f sortPos = new Vector3f((float) subtracted.x, (float) subtracted.y, (float) subtracted.z);
        return data.sortStates.get(renderType).buildSortedIndexBuffer(data.getByteBuffer(renderType), VertexSorting.byDistance(v -> -sortPos.distanceSquared(v)));
    }
}
