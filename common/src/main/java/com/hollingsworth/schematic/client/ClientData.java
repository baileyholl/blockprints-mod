package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.ClientConstants;
import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.gui.GuiUtils;
import com.hollingsworth.schematic.client.gui.HomeScreen;
import com.hollingsworth.schematic.client.gui.UploadPreviewScreen;
import com.hollingsworth.schematic.common.util.SchematicExport;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.KeyMapping;
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
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.atomic.AtomicReference;

import static com.hollingsworth.schematic.client.RaycastHelper.rayTraceRange;

public class ClientData {
    private static final AtomicReference<String> uploadStatus = new AtomicReference<>();
    public static boolean showBoundary;
    public static BlockPos firstTarget;
    public static BlockPos secondTarget;
    private static final String CATEGORY = "key.category." + Constants.MOD_ID + ".general";
    public static final KeyMapping OPEN_MENU = new KeyMapping("key." + Constants.MOD_ID + ".open_menu", GLFW.GLFW_KEY_GRAVE_ACCENT, CATEGORY);
    public static final KeyMapping CONFIRM = new KeyMapping("key." + Constants.MOD_ID + ".confirm_selection", GLFW.GLFW_KEY_ENTER, CATEGORY);
    public static final KeyMapping CANCEL = new KeyMapping("key." + Constants.MOD_ID + ".cancel_selection", GLFW.GLFW_KEY_BACKSPACE, CATEGORY);
    public static final KeyMapping[] KEYS = new KeyMapping[]{OPEN_MENU, CONFIRM, CANCEL};

    public static void openMenu() {
        Minecraft.getInstance().setScreen(new HomeScreen());
    }


    public static void setStatus(@NotNull Component component) {
        uploadStatus.set(component.getString());
    }

    public static void onConfirmHit() {
        if (!ClientData.showBoundary) {
            return;
        }
        ClientData.showBoundary = false;
        if (ClientData.firstTarget != null && ClientData.secondTarget != null) {
            StructureTemplate structure = SchematicExport.getStructure(Minecraft.getInstance().level, ClientData.firstTarget, ClientData.secondTarget);
            Minecraft.getInstance().setScreen(new UploadPreviewScreen(structure, ClientData.firstTarget, ClientData.secondTarget));
        }
    }

    public static void onCancelHit() {
        if (!ClientData.showBoundary) {
            return;
        }
        ClientData.showBoundary = false;
        ClientData.firstTarget = null;
        ClientData.secondTarget = null;
    }

    public static BlockPos selectedPos = null;

    public static void renderBoundary(PoseStack poseStack) {
        if (!ClientData.showBoundary)
            return;
        BlockPos firstPos = ClientData.firstTarget;
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
        BlockPos secondPos = ClientData.secondTarget;
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
        if (!ClientData.showBoundary) {
            return false;
        }
        BlockPos pos = selectedPos;
        if (pos == null) {
            return false;
        }
        if (ClientData.firstTarget == null) {
            ClientData.firstTarget = pos.immutable();
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable(Constants.MOD_ID + ".select_second"));
            return true;// test
        } else if (ClientData.secondTarget == null && !ClientData.firstTarget.equals(pos)) {
            ClientData.secondTarget = pos.immutable();
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable(Constants.MOD_ID + ".confirm_selection", CONFIRM.getTranslatedKeyMessage()));
            return true;
        }
        return false;
    }

    public static void renderBoundaryUI(GuiGraphics graphics, Window window) {
        String status = uploadStatus.get();
        if (status != null && !status.isEmpty()) {
            GuiUtils.drawOutlinedText(Minecraft.getInstance().font, graphics, Component.literal(status).getVisualOrderText(), 0, 20);
        }
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
