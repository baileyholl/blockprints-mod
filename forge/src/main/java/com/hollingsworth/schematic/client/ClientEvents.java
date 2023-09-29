package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.Constants;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.hollingsworth.schematic.client.RaycastHelper.rayTraceRange;
import static com.hollingsworth.schematic.client.RaycastHelper.rayTraceUntil;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Constants.MOD_ID)
@OnlyIn(Dist.CLIENT)
public class ClientEvents {

    private static final Minecraft MINECRAFT = Minecraft.getInstance();

    @SubscribeEvent
    public static void registerKeyBindings(RegisterKeyMappingsEvent event) {
        event.register(ClientData.OPEN_MENU);
    }

    @SubscribeEvent
    public static void keyEvent(final InputEvent.Key event) {
        if (Minecraft.getInstance().player == null || InputConstants.PRESS != event.getAction())
            return;
        if(MINECRAFT.screen == null && event.getKey() == ClientData.OPEN_MENU.getKey().getValue())
            ClientData.openMenu();
    }

    @SubscribeEvent
    public static void renderLast(final RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) {
            return;
        }
        if (!ClientData.showBoundary)
            return;
        BlockPos firstPos = ClientData.firstTarget;
        if (firstPos == null)
            return;
        BlockPos selectedPos = null;
        LocalPlayer player = Minecraft.getInstance().player;
        BlockHitResult trace = rayTraceRange(player.level(), player, 75);
        if (trace != null && trace.getType() == HitResult.Type.BLOCK) {

            BlockPos hit = trace.getBlockPos();
            boolean replaceable = player.level().getBlockState(hit)
                    .canBeReplaced(new BlockPlaceContext(new UseOnContext(player, InteractionHand.MAIN_HAND, trace)));
            if (trace.getDirection()
                    .getAxis()
                    .isVertical() && !replaceable)
                hit = hit.relative(trace.getDirection());
            selectedPos = hit;
        } else
            selectedPos = null;
        BlockPos secondPos = ClientData.secondTarget;
        if (secondPos == null) {
            secondPos = selectedPos;
        }
        Direction selectedFace = null;
        if (secondPos != null) {
            AABB bb = new AABB(firstPos, secondPos).expandTowards(1, 1, 1)
                    .inflate(.45f);
            Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera()
                    .getPosition();
            boolean inside = bb.contains(projectedView);
            RaycastHelper.PredicateTraceResult result =
                    rayTraceUntil(player, 70, pos -> inside ^ bb.contains(VecHelper.getCenterOf(pos)));
            selectedFace = result.missed() ? null
                    : inside ? result.getFacing()
                    .getOpposite() : result.getFacing();
        }

        AABB currentSelectionBox = null;
        if (secondPos == null) {
            if (firstPos == null) {
                currentSelectionBox = selectedPos == null ? null : new AABB(selectedPos);
            }else{
                currentSelectionBox = selectedPos == null ? new AABB(firstPos) : new AABB(firstPos, selectedPos).expandTowards(1, 1, 1);
            }
        }else {
            currentSelectionBox = new AABB(firstPos, secondPos).expandTowards(1, 1, 1);
        }

        if(currentSelectionBox != null){
            Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera()
                    .getPosition();
            boolean cameraInside = currentSelectionBox.contains(camera);
            float inflate = cameraInside ? -1 / 128f : 1 / 128f;

            currentSelectionBox.move(camera.scale(-1));
            currentSelectionBox = currentSelectionBox.move(-camera.x, -camera.y, -camera.z);

            PoseStack poseStack = event.getPoseStack();
            poseStack.pushPose();
            VertexConsumer vertexconsumer = Constants.bufferSource.getBuffer(RenderType.lines());
            LevelRenderer.renderLineBox(poseStack, vertexconsumer, currentSelectionBox, 0.9F, 0.9F, 0.9F, 1.0f);
            Constants.bufferSource.endBatch();
            poseStack.popPose();
        }
    }


}
