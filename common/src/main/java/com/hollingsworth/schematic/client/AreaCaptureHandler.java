package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.ClientConstants;
import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.gui.GuiUtils;
import com.hollingsworth.schematic.client.gui.UploadPreviewScreen;
import com.hollingsworth.schematic.common.util.SchematicExport;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import static com.hollingsworth.schematic.client.ClientData.*;
import static com.hollingsworth.schematic.client.RaycastHelper.rayTraceRange;

public class AreaCaptureHandler {

    public static BlockPos firstTarget;
    public static BlockPos secondTarget;
    public static boolean showBoundary;

    public static void startCapture(){
        AreaCaptureHandler.showBoundary = true;
        AreaCaptureHandler.firstTarget = null;
        AreaCaptureHandler.secondTarget = null;
    }

    public static void cancelCapture(){
        AreaCaptureHandler.showBoundary = false;
        AreaCaptureHandler.firstTarget = null;
        AreaCaptureHandler.secondTarget = null;
    }

    public static void onConfirmHit() {
        if (!AreaCaptureHandler.showBoundary) {
            return;
        }
        AreaCaptureHandler.showBoundary = false;
        if (AreaCaptureHandler.firstTarget != null && AreaCaptureHandler.secondTarget != null) {
            StructureTemplate structure = SchematicExport.getStructure(Minecraft.getInstance().level, AreaCaptureHandler.firstTarget, AreaCaptureHandler.secondTarget);
            Minecraft.getInstance().setScreen(new UploadPreviewScreen(structure, AreaCaptureHandler.firstTarget, AreaCaptureHandler.secondTarget));
        }
    }

    public static void onCancelHit() {
        if (!AreaCaptureHandler.showBoundary) {
            return;
        }
        cancelCapture();
    }

    public static BlockPos selectedPos = null;

    public static void renderBoundary(PoseStack poseStack) {
        if (!AreaCaptureHandler.showBoundary)
            return;
        BlockPos firstPos = AreaCaptureHandler.firstTarget;
        LocalPlayer player = Minecraft.getInstance().player;
        BlockHitResult trace = rayTraceRange(player.level(), player, 75);
        if (trace.getType() == HitResult.Type.BLOCK) {

            BlockPos hit = trace.getBlockPos();
            boolean replaceable = player.level().getBlockState(hit)
                    .canBeReplaced(new BlockPlaceContext(new UseOnContext(player, InteractionHand.MAIN_HAND, trace)));
            if (trace.getDirection()
                    .getAxis()
                    .isVertical() && !replaceable)
                hit = hit.relative(trace.getDirection());
            selectedPos = hit;
        } else {
            selectedPos = null;
        }
        if (firstPos == null && selectedPos != null) {
            renderBbox(new AABB(selectedPos), poseStack);
            return;
        }
        BlockPos secondPos = AreaCaptureHandler.secondTarget;
        if (secondPos == null) {
            secondPos = selectedPos;
        }
        AABB currentSelectionBox = null;
        if (secondPos == null) {
            if (firstPos == null) {
                currentSelectionBox = selectedPos == null ? null : new AABB(selectedPos);
            } else {
                currentSelectionBox = selectedPos == null ? new AABB(firstPos) : new AABB(firstPos, selectedPos).expandTowards(1, 1, 1);
            }
        } else {
            currentSelectionBox = new AABB(firstPos, secondPos).expandTowards(1, 1, 1);
        }

        renderBbox(currentSelectionBox, poseStack);
    }

    public static void renderBbox(AABB currentSelectionBox, PoseStack poseStack) {
        if (currentSelectionBox == null) {
            return;
        }
        Vec3 camera = Minecraft.getInstance().gameRenderer.getMainCamera()
                .getPosition();

        currentSelectionBox.move(camera.scale(-1));
        currentSelectionBox = currentSelectionBox.move(-camera.x, -camera.y, -camera.z);

        poseStack.pushPose();
        VertexConsumer vertexconsumer = ClientConstants.bufferSource.getBuffer(RenderType.lines());
        LevelRenderer.renderLineBox(poseStack, vertexconsumer, currentSelectionBox, 0.9F, 0.9F, 0.9F, 1.0f);
        ClientConstants.bufferSource.endBatch();
        poseStack.popPose();
    }

    public static boolean positionClicked() {
        if (!AreaCaptureHandler.showBoundary) {
            return false;
        }
        BlockPos pos = selectedPos;
        if (pos == null) {
            return false;
        }
        if (AreaCaptureHandler.firstTarget == null) {
            AreaCaptureHandler.firstTarget = pos.immutable();
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable(Constants.MOD_ID + ".select_second"));
            return true;// test
        } else if (AreaCaptureHandler.secondTarget == null && !AreaCaptureHandler.firstTarget.equals(pos)) {
            AreaCaptureHandler.secondTarget = pos.immutable();
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable(Constants.MOD_ID + ".confirm_selection", CONFIRM.getTranslatedKeyMessage()));
            return true;
        }
        return false;
    }

    public static void renderBoundaryUI(GuiGraphics graphics, Window window) {
        if (!showBoundary)
            return;
        float screenY = window.getGuiScaledHeight() / 2f;
        float screenX = window.getGuiScaledWidth() / 2f;
        float instructionY = window.getGuiScaledHeight() - 42;
        graphics.pose().pushPose();
        graphics.pose().translate(screenX, instructionY, 0);
        if (firstTarget != null && secondTarget != null) {
            GuiUtils.drawCenteredOutlinedText(Minecraft.getInstance().font, graphics, Component.translatable(Constants.MOD_ID + ".confirm_selection", CONFIRM.getTranslatedKeyMessage()).getVisualOrderText(), 0, 0);
        } else {
            String compKey = firstTarget == null ? "select_first" : "select_second";
            GuiUtils.drawCenteredOutlinedText(Minecraft.getInstance().font, graphics, Component.translatable(Constants.MOD_ID + "." + compKey).getVisualOrderText(), 0, 0);
        }
        graphics.pose().popPose();
        graphics.pose().pushPose();
        graphics.pose().translate(screenX,  instructionY+ 10, 0);
        GuiUtils.drawCenteredOutlinedText(Minecraft.getInstance().font, graphics, Component.translatable(Constants.MOD_ID + ".cancel_selection", CANCEL.getTranslatedKeyMessage()).getVisualOrderText(), 0, 0);
        graphics.pose().popPose();
    }

}
