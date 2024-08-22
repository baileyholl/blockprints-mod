package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.Constants;
import com.hollingsworth.schematic.client.gui.GuiUtils;
import com.hollingsworth.schematic.client.renderer.StructureRenderData;
import com.hollingsworth.schematic.client.renderer.StructureRenderer;
import com.hollingsworth.schematic.network.PlaceStructurePacket;
import com.hollingsworth.schematic.platform.Services;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import static com.hollingsworth.schematic.client.ClientData.CANCEL;
import static com.hollingsworth.schematic.client.ClientData.CONFIRM;

public class RenderStructureHandler {
    public static BlockPos anchorPos;
    public static StructureRenderData placingData;

    public static void startRender(StructureTemplate structureTemplate, String name, String bpId){
        if(placingData != null){
            cancelRender();
        }
        anchorPos = null;
        placingData = new StructureRenderData(structureTemplate, name, bpId);
        StructureRenderer.structures.add(placingData);
    }

    public static void cancelRender(){
        if(placingData == null){
            return;
        }
        anchorPos = null;
        StructureRenderer.structures.remove(placingData);
        placingData = null;
    }

    public static void onConfirmHit() {
        if (placingData == null) {
            return;
        }
        Services.PLATFORM.sendClientToServerPacket(new PlaceStructurePacket(placingData.statePosCache));
        placingData = null;
    }

    public static void onCancelHit() {
        if (placingData == null) {
            return;
        }
        cancelRender();
    }

    public static void positionClicked() {
        if (placingData == null) {
            return;
        }
        placingData.anchorPos = RaycastHelper.getLookingAt(Minecraft.getInstance().player, true).getBlockPos();
    }

    public static void onRotateHit(boolean clockwise) {
        if (placingData == null) {
            return;
        }
        placingData.rotate(clockwise ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
    }

    public static void onMirrorHit() {
        if (placingData == null) {
            return;
        }
        placingData.mirror(true);
    }

    public static void renderInstructions(GuiGraphics graphics, Window window) {
        if (placingData == null)
            return;
        float screenY = window.getGuiScaledHeight() / 2f;
        float screenX = window.getGuiScaledWidth() / 2f;
        float instructionY = window.getGuiScaledHeight() - 42;
        graphics.pose().pushPose();
        graphics.pose().translate(screenX, instructionY, 0);

        GuiUtils.drawCenteredOutlinedText(Minecraft.getInstance().font, graphics, Component.translatable(Constants.MOD_ID + ".confirm_selection", CONFIRM.getTranslatedKeyMessage()).getVisualOrderText(), 0, 0);

        graphics.pose().popPose();
        graphics.pose().pushPose();
        graphics.pose().translate(screenX,  instructionY+ 10, 0);
        GuiUtils.drawCenteredOutlinedText(Minecraft.getInstance().font, graphics, Component.translatable(Constants.MOD_ID + ".cancel_selection", CANCEL.getTranslatedKeyMessage()).getVisualOrderText(), 0, 0);
        graphics.pose().popPose();
    }
}
