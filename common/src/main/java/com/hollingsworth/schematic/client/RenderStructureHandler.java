package com.hollingsworth.schematic.client;

import com.hollingsworth.schematic.client.gui.PlaceSchematicScreen;
import com.hollingsworth.schematic.client.renderer.StructureRenderData;
import com.hollingsworth.schematic.client.renderer.StructureRenderer;
import com.hollingsworth.schematic.networking.PlaceSchematicPacket;
import com.hollingsworth.schematic.platform.Services;
import com.mojang.blaze3d.platform.Window;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

public class RenderStructureHandler {
    public static StructureRenderData placingData;
    public static PlaceSchematicScreen schematicTools = new PlaceSchematicScreen();

    public static void tick(){
        if(placingData == null){
            return;
        }
        schematicTools.update();
    }

    public static void startRender(StructureTemplate structureTemplate, String name, String bpId){
        if(placingData != null){
            cancelRender();
        }
        placingData = new StructureRenderData(structureTemplate, name, bpId);
        schematicTools = new PlaceSchematicScreen();
        StructureRenderer.structures.add(placingData);
    }

    public static void cancelRender(){
        if(placingData == null){
            return;
        }
        StructureRenderer.structures.remove(placingData);
        placingData = null;
    }

    public static void onConfirmHit() {
        if (placingData == null) {
            return;
        }
        placingData = null;
    }

    public static void setAnchor(){
        if(placingData == null){
            return;
        }
        placingData.anchorPos = RaycastHelper.getLookingAt(Minecraft.getInstance().player, true).getBlockPos();
        schematicTools.setupManipulationTools();
    }

    public static void positionClicked() {
        if (placingData == null) {
            return;
        }
        schematicTools.getSelectedElement().onClick();
    }

    public static void onRotateHit(boolean clockwise) {
        if (placingData == null) {
            return;
        }
        placingData.rotate(clockwise ? Rotation.CLOCKWISE_90 : Rotation.COUNTERCLOCKWISE_90);
        placingData.lastRenderPos = null;
    }

    public static void offsetAnchor(BlockPos pos){
        if(placingData == null || placingData.anchorPos == null){
            return;
        }
        placingData.anchorPos = placingData.anchorPos.offset(pos);
    }

    public static void onFlip() {
        if (placingData == null) {
            return;
        }
        placingData.flip();
        placingData.lastRenderPos = null;
    }

    public static void toolKeyHit(KeyEvent event){
        if (placingData == null) {
            return;
        }
        boolean pressed = event.isDown();
        if (pressed && !schematicTools.focused)
            schematicTools.focused = true;
        if (!pressed && schematicTools.focused) {
            schematicTools.focused = false;
            schematicTools.onClose();
        }
    }

    public static boolean mouseScrolled(double delta) {
        if (placingData == null) {
            return false;
        }
        return schematicTools.scroll(delta);
    }

    public static void renderInstructions(GuiGraphics graphics, Window window) {
        if (placingData == null)
            return;
        schematicTools.renderPassive(graphics, 0);
    }

    public static void placeOnServer(){
        if(placingData == null){
            return;
        }
        Services.PLATFORM.sendClientToServerPacket(new PlaceSchematicPacket(placingData.structureTemplate, placingData.structurePlaceSettings, placingData.anchorPos.above(1)));
    }
}
