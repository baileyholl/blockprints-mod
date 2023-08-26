package com.hollingsworth.schematic.client;

//public class BuildingRenderer implements BlockEntityRenderer<BuildingEntity> {
//
//    public BuildingRenderer(BlockEntityRendererProvider.Context pContext) {
//
//    }
//
//    public void render(BuildingEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
//        if(pBlockEntity.getArea() == null){
//            return;
//        }
//        AABB renderBB = pBlockEntity.getArea();
//        if(renderBB == null)
//            return;
//        BlockPos deskPos = pBlockEntity.getBlockPos();
//        renderBB = renderBB.move(deskPos.multiply(-1));
//        VertexConsumer vertexconsumer = pBufferSource.getBuffer(RenderType.lines());
//        LevelRenderer.renderLineBox(pPoseStack, vertexconsumer, renderBB, 0.9F, 0.9F, 0.9F, 1.0f);
//
//    }
//
//    public boolean shouldRenderOffScreen(ManagementDeskEntity pBlockEntity) {
//        return true;
//    }
//
//    public int getViewDistance() {
//        return 96;
//    }
//}
