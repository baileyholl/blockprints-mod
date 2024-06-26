package com.hollingsworth.schematic.client.renderer;

import com.hollingsworth.schematic.client.RaycastHelper;
import com.hollingsworth.schematic.common.util.Color;
import com.hollingsworth.schematic.common.util.DimPos;
import com.hollingsworth.schematic.platform.Services;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;
import com.mojang.blaze3d.vertex.VertexSorting;
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
import java.util.stream.Collectors;

public class StructureRenderer {
    public static ArrayList<StructureRenderData> structures = new ArrayList<>();
    private static int sortCounter = 0;

    //A map of RenderType -> DireBufferBuilder, so we can draw the different render types in proper order later
    private static final Map<RenderType, DireBufferBuilder> builders = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((renderType) -> renderType, (type) -> new DireBufferBuilder(type.bufferSize())));
    //A map of RenderType -> Vertex Buffer to buffer the different render types.
    private static final Map<RenderType, VertexBuffer> vertexBuffers = RenderType.chunkBufferLayers().stream().collect(Collectors.toMap((renderType) -> renderType, (type) -> new VertexBuffer(VertexBuffer.Usage.STATIC)));

    //Get the buffer from the map, and ensure its building
    public static DireBufferBuilder getBuffer(RenderType renderType) {
        final DireBufferBuilder buffer = builders.get(renderType);
        if (!buffer.building()) {
            buffer.begin(renderType.mode(), renderType.format());
        }
        return buffer;
    }

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
        if (shouldUpdateRender(player)) {
            generateRender(data, player.level(), renderPos, 0.5f, data.statePosCache, vertexBuffers);
            data.updateRender = false;
        }
    }

    public static boolean shouldUpdateRender(Player player) {
//        ArrayList<StatePos> buildList;
//        BaseMode mode = GadgetNBT.getMode(gadget);
//        BlockHitResult lookingAt = RaycastHelper.getLookingAt(player, true);
//        BlockPos anchorPos = GadgetNBT.getAnchorPos(gadget);
//        BlockPos renderPos = anchorPos.equals(GadgetNBT.nullPos) ? lookingAt.getBlockPos() : anchorPos;
//        UUID gadgetUUID = GadgetNBT.getUUID(gadget);
//
////        if (gadget.getItem() instanceof GadgetBuilding || gadget.getItem() instanceof GadgetExchanger) {
////            if (player.level().getBlockState(renderPos).isAir())
////                return false;
////            BlockState renderBlockState = GadgetNBT.getGadgetBlockState(gadget);
////            if (renderBlockState.isAir()) return false;
////            buildList = mode.collect(lookingAt.getDirection(), player, renderPos, renderBlockState); //Get the build list for what we're looking at
////
////            FakeRenderingWorld tempWorld = new FakeRenderingWorld(player.level(), buildList, renderPos); //Toss it into the fake render world
////            if (fakeRenderingWorld != null && fakeRenderingWorld.positions.equals(tempWorld.positions)) //If they are identical, no need to update render
////                return false;
////
////            //If not, we should update the cache, the UUID, and return true, meaning we need to update the render
////            statePosCache = buildList;
////            copyPasteUUIDCache = UUID.randomUUID(); //In case theres an existing copy/Paste render saved, nullify it
////            return true;
////        } else if (gadget.getItem() instanceof GadgetCopyPaste || gadget.getItem() instanceof GadgetCutPaste) {
//            renderPos = renderPos.above();
//            renderPos.offset(GadgetNBT.getRelativePaste(gadget));
//            if (mode.getId().getPath().equals("paste")) { //Paste Mode Only
//                if (!BG2DataClient.isClientUpToDate(gadget)) { //Have the BG2DataClient class check if its up to date
//                    return false; //If not up to date, we need to return false, since theres no need to regen the render if its out of date! We'll check again next draw frame
//                }
//                UUID BG2ClientUUID = BG2DataClient.getCopyUUID(gadgetUUID);
//                if (BG2ClientUUID != null && copyPasteUUIDCache.equals(BG2ClientUUID)) //If the cache this class has matches the client cache for this gadget, no need to rebuild
//                    return false;
//                //If we get here, the copy paste we have stored here differs from whats in the client AND the client is up to date, so rebuild!
//                copyPasteUUIDCache = BG2ClientUUID; //Cache the new copyPasteUUID for next cycle
//                statePosCache = BG2DataClient.getLookupFromUUID(gadgetUUID);
//                return true; //Need a render update!
//            }
////        } else { //Not a gadget that needs updates
////            return false;
////        }
        return true;
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
                DireVertexConsumer direVertexConsumer = new DireVertexConsumer(getBuffer(renderType), transparency);
                //Use tesselateBlock to skip the block.isModel check - this helps render Create blocks that are both models AND animated
                if (renderState.getFluidState().isEmpty()) {
                    //modelBlockRenderer.tesselateBlock(level, ibakedmodel, renderState, pos.pos.offset(renderPos).above(255), matrix, direVertexConsumer, false, random, renderState.getSeed(pos.pos.offset(renderPos)), OverlayTexture.NO_OVERLAY, ModelData.EMPTY, renderType);
                    try {
                        modelBlockRenderer.tesselateBlock(data.fakeRenderingWorld, ibakedmodel, renderState, pos.pos.offset(renderPos).above(255), matrix, direVertexConsumer, false, random, renderState.getSeed(pos.pos.offset(renderPos)), OverlayTexture.NO_OVERLAY);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //System.out.println(e);
                    }
                } else {
                    RenderFluidBlock.renderFluidBlock(renderState, level, pos.pos.offset(renderPos).above(255), matrix, direVertexConsumer, true);
                }
            }
            matrix.popPose();
        }
        //Sort all the builder's vertices and then upload them to the vertex buffers
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 subtracted = projectedView.subtract(renderPos.getX(), renderPos.getY(), renderPos.getZ());
        Vector3f sortPos = new Vector3f((float) subtracted.x, (float) subtracted.y, (float) subtracted.z);
        for (Map.Entry<RenderType, DireBufferBuilder> entry : builders.entrySet()) {
            RenderType renderType = entry.getKey();
            DireBufferBuilder direBufferBuilder = getBuffer(renderType);
            direBufferBuilder.setQuadSorting(VertexSorting.byDistance(sortPos));
            data.sortStates.put(renderType, direBufferBuilder.getSortState());
            VertexBuffer vertexBuffer = vertexBuffers.get(entry.getKey());
            vertexBuffer.bind();
            vertexBuffer.upload(direBufferBuilder.end());
            VertexBuffer.unbind();
        }
    }

    public static void drawCopyBox(PoseStack matrix, BlockPos start, BlockPos end) {
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        matrix.pushPose();
        matrix.translate(-projectedView.x(), -projectedView.y(), -projectedView.z());
        Color color = Color.GREEN;// mode.equals("copy") ? Color.GREEN : Color.RED;
        DireRenderMethods.renderCopy(matrix, start, end, color);

        matrix.popPose();
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
    public static void drawRender(StructureRenderData data, PoseStack poseStack, Matrix4f projectionMatrix, Player player) {
        if (vertexBuffers == null) {
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
        if (sortCounter > 20) {
            sortAll(data, renderPos);
            sortCounter = 0;
        } else {
            sortCounter++;
        }

        PoseStack matrix = poseStack;
        matrix.pushPose();
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
                VertexBuffer vertexBuffer = vertexBuffers.get(renderType);
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

        //Fluid Rendering
        /*for (StatePos pos : statePosCache.stream().filter(pos -> (!pos.state.getFluidState().isEmpty())).toList()) {
            matrix.pushPose();
            matrix.translate(-projectedView.x(), -projectedView.y(), -projectedView.z());
            matrix.translate(renderPos.getX(), renderPos.getY(), renderPos.getZ());
            FluidState fluidstate = pos.state.getFluidState();
            RenderType rendertype = ItemBlockRenderTypes.getRenderLayer(fluidstate);
            DireVertexConsumer direVertexConsumer = new DireVertexConsumer(buffersource.getBuffer(rendertype), 0.5f);
            dispatcher.renderLiquid(pos.pos, player.level(), direVertexConsumer, pos.state, fluidstate);
            buffersource.endBatch(rendertype);
            matrix.popPose();
        }*/

        //Red Overlay for missing Items
//        boolean hasBound = GadgetNBT.getBoundPos(gadget) != null;
//        BlockState renderBlockState = GadgetNBT.getGadgetBlockState(gadget);
//        if (!player.isCreative() && !hasBound && renderBlockState.getFluidState().isEmpty()) {
//
////            ItemStack findStack = GadgetUtils.getItemForBlock(renderBlockState, player.level(), BlockPos.ZERO, player);
////            int availableItems = BuildingUtils.countItemStacks(player, findStack);
////            int energyStored = BuildingUtils.getEnergyStored(gadget);
////            int energyCost = BuildingUtils.getEnergyCost(gadget);
//            for (StatePos statePos : buildList) {
////                if (availableItems <= 0 || energyStored < energyCost) {
//                matrix.pushPose();
//                matrix.translate(-projectedView.x(), -projectedView.y(), -projectedView.z());
//                matrix.translate(renderPos.getX(), renderPos.getY(), renderPos.getZ());
//                VertexConsumer builder = buffersource.getBuffer(DireRenderTypes.MissingBlockOverlay);
//                //if (hasBound)
//                //    MyRenderMethods.renderBoxSolid(evt.getPoseStack().last().pose(), builder, statePos.pos, 1, 1, 0, 0.35f);
//                //else
//                DireRenderMethods.renderBoxSolid(poseStack.last().pose(), builder, statePos.pos, 1, 0, 0, 0.35f);
//                matrix.popPose();
////              }
////                availableItems--;
////                energyStored -= energyCost;
//            }
//        }
    }

    //Sort all the RenderTypes
    public static void sortAll(StructureRenderData data, BlockPos lookingAt) {
        for (Map.Entry<RenderType, BufferBuilder.SortState> entry : data.sortStates.entrySet()) {
            RenderType renderType = entry.getKey();
            BufferBuilder.RenderedBuffer renderedBuffer = sort(data, lookingAt, renderType);
            VertexBuffer vertexBuffer = vertexBuffers.get(renderType);
            vertexBuffer.bind();
            vertexBuffer.upload(renderedBuffer);
            VertexBuffer.unbind();
        }
    }

    //Sort the render type we pass in - using DireBufferBuilder because we want to sort in the opposite direction from normal
    public static BufferBuilder.RenderedBuffer sort(StructureRenderData data, BlockPos lookingAt, RenderType renderType) {
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 subtracted = projectedView.subtract(lookingAt.getX(), lookingAt.getY(), lookingAt.getZ());
        Vector3f sortPos = new Vector3f((float) subtracted.x, (float) subtracted.y, (float) subtracted.z);
        DireBufferBuilder bufferBuilder = getBuffer(renderType);
        BufferBuilder.SortState sortState = data.sortStates.get(renderType);
        bufferBuilder.restoreSortState(sortState);
        bufferBuilder.setQuadSorting(VertexSorting.byDistance(sortPos));
        data.sortStates.put(renderType, bufferBuilder.getSortState());
        return bufferBuilder.end();
    }
}
